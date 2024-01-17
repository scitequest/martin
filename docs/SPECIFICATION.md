# Export File Structure Specification

Current version of the specification is **0.7.0**.
As such this is still a draft and feedback is welcome since ambiguities are likely.
The version is stored in the `metadata.json` as noted below to ensure the parsing application is able to know the standard used during the export.

## File Structure

Exporting a measurement will store the files in the following file system directory called a _measurement folder_.
Here the project name and sample ID are transformed into kebap-case.

```text
<project-name>/<YYYY-MM-DD>/hh-mm-ss-<sample-id>
```

## Measurement Folder

Depending on the settings chosen, the following files will be exported:

- `metadata.json`
- `parameters.json`
- `image.<extension>`
- `annotated_image.tiff`
- `data.json`
- `data.tsv`
- `data_statistics.json`
- `data_statistics.tsv`

Note: the specification for all relevant JSON files is provided as JSON Schema[^1] in the `src/tests/resources/schema` directory.

The `metadata.json` hold all metadata information about a measurement.
It also contains the version number of this specification used to store the measurement in the file structure.

`parameters.json` is internal and used to perform an integrity check on the measurement folder.
Therefore, it is of no interest to post-processing or data analysis.

A copy of the original image is provided in the `image` file, which retains the original extension.
We store this to be able to perform post-measurement validation such as the integrity check.

Similarly, the `annotated_image.tiff` provides a cropped image with the slide mask overlaid to enable human verification that the slide mask was placed correctly.

The data files contain the actual measurement data.
For the meaning of the values we refer to the paper.
Files are provided in JSON and TSV format.
The TSV format is already well-known and should be self-explanatory - delimiters are the TAB character (`\t`), newlines are LF only (`\n`).

[^1]: <https://json-schema.org/>
