#! /bin/sh
cp config/application-template.properties config/application.properties 
cp manifest.yml.template manifest.yml
sed -i 's/..\/..\/..\/..\/pages\/adoption/http:\/\/predixdev.github.io/g' README.md
sed -i 's/predix-asset-sysint.grc-apps.svc.ice.ge.com/predix-asset.run.aws-usw02-pr.ice.predix.io/g' config/com.ge.predix.solsvc.dataexchange.config.config


