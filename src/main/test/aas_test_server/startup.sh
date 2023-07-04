#!/bin/bash
set -m

java -jar /usr/share/basyxExecutable.jar &

AAS_SERVER_URL='http://localhost:4001/aasServer/shells'
SM_ID_SHORT=$(jq -r .idShort /opt/consul_info_sm.json)

echo "Test availability of AAS Server @ " $AAS_SERVER_URL
until curl -m 5 -s --location --request GET "$AAS_SERVER_URL" > /dev/null; do
  echo "AAS Server is unavailable -> sleeping"
  sleep 1
done

sed -ir 's/{{aas_id}}/'$AAS_ID'/g' /opt/consul_info_sm.json
sed -ir 's/{{aas_id}}/'$AAS_ID'/g' /opt/aas.json
sed -ir 's/{{resource_ip}}/'$RESOURCE_IP'/g' /opt/consul_info_sm.json

#CONSUL_INFO_SM=$(cat /opt/consul_info_sm.json)
#CONSUL_INFO_SM=$(jq '.identification.id="ConsulInfo-'$AAS_ID'"' <<< "$CONSUL_INFO_SM")
#CONSUL_INFO_SM=$(jq '(.submodelElements[] | select(.idShort == "IP")).value="'$RESOURCE_IP'"' <<< "$CONSUL_INFO_SM")
#CONSUL_INFO_SM=$(jq '(.submodelElements[].parent | select(.type == "AssetAdministrationShell")).value="'$AAS_ID'"' <<< "$CONSUL_INFO_SM")
#CONSUL_INFO_SM=$(jq '(.submodelElements[].parent | select(.type == "Submodul")).value="ConsulInfo-'$AAS_ID'"' <<< "$CONSUL_INFO_SM")
#echo $CONSUL_INFO_SM > /opt/consul_info_sm.json

curl --location --request PUT "$AAS_SERVER_URL/$AAS_ID" -H "Content-Type: application/json" -d @/opt/aas.json
curl --location --request PUT "$AAS_SERVER_URL/$AAS_ID/aas/submodels/$SM_ID_SHORT" -H "Content-Type: application/json" -d @/opt/consul_info_sm.json

fg %1