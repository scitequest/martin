"""Compile all Markdown files in the current directory into HTML files."""
import logging
import shutil
import sys
from pathlib import Path

import markdown

TARGET_PATH = Path("target")
HTML_OUT = TARGET_PATH / "html"

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


def try_main(argv):
    """Actual main."""
    logger.info("Cleaning up HTML directory")
    shutil.rmtree(HTML_OUT, ignore_errors=True)

    logger.info("Converting Markdown files in the base directory")
    md = markdown.Markdown(extensions=["extra"])
    for filepath in argv:
        md_file = Path(filepath)
        if md_file.is_absolute():
            html_file = HTML_OUT / md_file.with_suffix(".html").name
        else:
            html_file = HTML_OUT / md_file.with_suffix(".html")
        html_file.parent.mkdir(exist_ok=True)
        logger.debug("Converting '%s'", md_file)
        contents_md = md_file.read_text(encoding="utf-8")
        contents_html = md.convert(contents_md)
        html_file.write_text(contents_html, encoding="utf-8", newline="\n", errors="xmlcharrefreplace")

    logger.info("Done :)")


def main(argv):
    """Main."""
    init_logging()
    logger.debug("Starting with arguments '%s'", argv)

    try:
        try_main(argv)
    except Exception as e:
        logger.error(e)
        sys.exit(1)


if __name__ == "__main__":
    main(sys.argv[1:])
