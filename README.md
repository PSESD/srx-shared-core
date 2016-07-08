# SRX Shared Core
***
Core Student Record Exchange functions shared by SRX components and services.

***
## Configuration
***

### Environment variables
Use of this library requires several environment variables to be set.
These must be set on the target host environment for deployment.
Alternately, for local development, create a file named 'env-local.properties' in the project root and add them there.

Following is a description of each variable:

Variable 					| Description 																									| Example
--------- 					| ----------- 																									| -------
ENVIRONMENT 				| Name of deployment environment (i.e. development, test, production). Set to 'local' for local development.	| local
LOG_LEVEL 					| Level of logging for named environment (debug, info, warning, error, critical).								| debug
ROLLBAR_ACCESS_TOKEN 		| Access token for Rollbar web service (required by Logger).													| (see PSESD administrator)
ROLLBAR_URL 				| Url for Rollbar web service (required by Logger).																| https://api.rollbar.com/api/1/item/
SERVER_NAME 				| Host server name (use 'localhost' for local development).														| localhost


***
## Copyright
***

Copyright (c) 2016 Puget Sound Educational Service District

This project is released under the [MIT License](https://github.com/PSESD/srx-shared-core/blob/master/LICENSE.md).

********