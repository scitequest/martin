"""Build a native installer for an Java application using jpackage including a custom JVM."""
import argparse
import logging
import os
import shutil
import subprocess
import sys
import urllib.request
import zipfile
from pathlib import Path

TARGET_PATH = Path("target")
INSTALLER_PATH = TARGET_PATH / "installer"
LIBS_PATH = INSTALLER_PATH / "libs"
JVM_PATH = INSTALLER_PATH / "jvm"

WIX_URL = "https://github.com/wixtoolset/wix3/releases/download/wix3112rtm/wix311-binaries.zip"
WIX_PATH = INSTALLER_PATH / "wix"

logger = logging.getLogger(__name__)


def init_logging():
    """Initialize the logging for this module."""
    root_logger = logging.getLogger()
    root_logger.setLevel(logging.DEBUG)
    log_format = "[%(asctime)s] %(levelname)s: %(message)s"
    formatter = logging.Formatter(log_format)
    handler = logging.StreamHandler()
    handler.setFormatter(formatter)
    root_logger.addHandler(handler)


def parse_args(argv):
    """Parse the arguments to the script."""
    parser = argparse.ArgumentParser(
        "Build a installer using jpackage including a custom JVM build"
    )
    parser.add_argument("main_jar", help="The path to the main JAR file to install")
    parser.add_argument(
        "main_class", help="The Java main class which is the entrypoint"
    )
    parser.add_argument(
        "--jdk-home",
        required=True,
        help="The Java home for the JDK that is used to build the installer."
        " If not specified this will use the environment variable JAVA_HOME.",
    )
    parser.add_argument(
        "--jre-home",
        required=True,
        help="The Java home for the JRE which is used as a base for the"
        " custom JVM runtime. If left unspecified, uses the JAVA_HOME environment variable.",
    )
    parser.add_argument(
        "--jre-version",
        required=True,
        type=int,
        help="The major version of the JRE such as '11'",
    )
    parser.add_argument(
        "--license-name",
        help="The license name as SPDX identifier",
    )
    os_group = parser.add_mutually_exclusive_group(required=True)
    os_group.add_argument(
        "--windows", action="store_true", help="Build a windows MSI installer"
    )
    os_group.add_argument(
        "--linux", action="store_true", help="Build a Linux RPM install file"
    )
    parser.add_argument(
        "jpackage_extra",
        nargs="*",
        help="Arguments passed to jpackage. Use an argument break `--`.",
    )
    args = parser.parse_args(argv)

    if not args.jdk_home:
        args.jdk_home = os.environ.get("JAVA_HOME")
        if not args.jdk_home:
            logger.error("JDK home and JAVA_HOME is not set")
            sys.exit(1)
    if not args.jre_home:
        args.jre_home = args.jdk_home
    args.main_jar = Path(args.main_jar).absolute().resolve()
    args.jdk_home = Path(args.jdk_home).absolute().resolve()
    args.jre_home = Path(args.jre_home).absolute().resolve()

    return args


def package_linux(args, params):
    """Builds the installer for Linux as RPM."""
    logger.info("Packaging Linux RPM installer")
    params.extend(["--type", "rpm", "--linux-shortcut"])
    if args.license_name:
        params.extend(["--linux-rpm-license-type", args.license_name])
    logger.debug("jpackage parameters are '%s'", params)
    subprocess.run(params, check=True)


def download_wix():
    """Downloads the WiX release version from WIX_URL."""
    logger.info("Downloading WiX")
    filehandle, _ = urllib.request.urlretrieve(WIX_URL)
    zipfile.ZipFile(filehandle, "r").extractall(path=WIX_PATH)


def package_windows(_args, params):
    """Builds the installer for Windows as MSI."""
    logger.info("Starting packaging Windows MSI installer")

    if not WIX_PATH.exists():
        download_wix()

    logger.info("Adding WiX toolset to PATH environment variable")
    os.environ["PATH"] += os.pathsep + str(WIX_PATH)

    logger.info("Calling jpackage to package the MSI installer")
    params.extend(
        [
            "--type",
            "msi",
            "--win-dir-chooser",
            "--win-shortcut",
            "--win-shortcut-prompt",
            "--win-per-user-install",
            "--win-menu",
        ]
    )
    logger.debug("jpackage parameters are '%s'", params)
    subprocess.run(params, check=True)


def try_main(args):
    """Actual main."""
    logger.info("Using JDK home '%s'", args.jdk_home)
    logger.info("Using JRE home '%s'", args.jre_home)

    logger.info("Cleaning up installer directory")
    shutil.rmtree(INSTALLER_PATH, ignore_errors=True)
    shutil.copytree(TARGET_PATH / "libs", LIBS_PATH)
    shutil.copy(args.main_jar, LIBS_PATH)

    logger.info("Detecting required modules")
    params = [
        str(args.jdk_home / "bin/jdeps"),
        "-q",
        "--multi-release",
        str(args.jre_version),
        "--ignore-missing-deps",
        "--print-module-deps",
        "--class-path",
        *map(str, (LIBS_PATH).glob("*")),
    ]
    logger.debug("jdeps parameters are '%s'", params)
    out = subprocess.run(params, check=True, capture_output=True)
    detected_modules = out.stdout.decode("utf-8").strip()
    logger.info("Detected modules '%s'", detected_modules)

    logger.info("Creating custom Java runtime")
    params = [
        str(args.jre_home / "bin/jlink"),
        "--module-path",
        str(args.jre_home / "jmods"),
        "--add-modules",
        detected_modules,
        "--strip-native-commands",
        "--no-header-files",
        "--no-man-pages",
        "--compress=2",
        "--strip-debug",
        "--output",
        str(JVM_PATH),
    ]
    logger.debug("jlink parameters are '%s'", params)
    subprocess.run(params, check=True)

    params = [
        str(args.jdk_home / "bin/jpackage"),
        "--input",
        str(LIBS_PATH),
        "--dest",
        str(INSTALLER_PATH),
        "--runtime-image",
        str(JVM_PATH),
        "--java-options",
        "-Xmx4096m",
        "--main-jar",
        str(args.main_jar),
        "--main-class",
        args.main_class,
        *args.jpackage_extra,
    ]
    if args.linux:
        package_linux(args, params)
    if args.windows:
        package_windows(args, params)

    logger.info("Done :)")


def main(argv):
    """Main."""
    init_logging()
    logger.debug("Starting with arguments '%s'", argv)
    args = parse_args(argv)
    logger.debug(args)

    try:
        try_main(args)
    except Exception as e:
        logger.error(e)
        sys.exit(1)


if __name__ == "__main__":
    main(sys.argv[1:])
