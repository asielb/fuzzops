UPDATE 4/6/2011: I have begun a massive code rewrite. I will commit the entire thing once I have a release candidate working. - Skyler

This tool fully automated the enumeration and fuzzing of web apps and services.

This level of automation has never been done before. It was built to be fire and forget, as well as centrally distributed and available for corporate build testing.

Will handle web apps (including AJAX), as well as SOAP.

Utilizes ARC4 as its means of generating fuzz data.

This tool is also modular to allow easy expansion of server commands, and the creation of fuzzing modules capable of learning from the results of other modules.

Discuss new ideas on our [Development mailing list](http://groups.google.com/group/fuzzops-dev).