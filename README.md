# OpenSpotlightDataExchange
A project that implements an open-source library to interface with Vectorworks Spotlight via the Lightwright Data Exchange format

## Current Status
Currently the project is a sketch of how the import works and is not really usable, however 
it will be implemented as a standalone Java library that can be integrated into other projects

## Goals
 - Allow an outside database or application to interface directly with Vectorworks Spotlight to 
 allow quick edits to the LightInfo record
 - Create a framework for reading data-exchange files into ANY application. 
 - Provide detailed documentation to the community on how this works so we can get some different 
 tools into the professional workflow
 - Start an open community for the users of Vectorworks Spotlight who have ideas on hwo to build better tools.
 
# Roadmap

## version 1.0 (LGPL)
 - Get the import working in Java with a full set of supporting unit tests
 - Provide clear documentation on how this exchange happens 
 
## version 2.0 (LGPL)
 - Create a native library in C++/Qt for export and use in other applications.
 - Integrate with Dropbox so that paperwork can be updated remotely.
