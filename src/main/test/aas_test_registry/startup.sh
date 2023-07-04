#!/bin/bash
set -m

java -jar /usr/share/basyxExecutable.jar &

AAS_REGISTRY_URL='http://localhost:4000/registry/api/v1/registry'

until curl -m 5 -s --location --request GET "$AAS_REGISTRY_URL" > /dev/null; do
  echo "AAS Registry is unavailable -> sleeping"
  sleep 1
done

sed -ir 's/{{aas_id}}/'$AAS_ID'/g' /opt/aas.json
sed -ir 's/{{resource_ip}}/'$RESOURCE_IP'/g' /opt/aas.json
sed -ir 's/{{aas_server_port}}/'$AAS_SERVER_PORT'/g' /opt/aas.json

curl --location --request PUT "$AAS_REGISTRY_URL/$AAS_ID" -H "Content-Type: application/json" -d @/opt/aas.json

fg %1