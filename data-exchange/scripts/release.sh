#! /bin/sh
cp config/application-template.properties config/application.properties 
cp manifest.yml.template manifest.yml
rm -rf manifest-integration.yml
rm -rf manifest-dataexchange.yml
