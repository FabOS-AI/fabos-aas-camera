FROM eclipsebasyx/aas-server:1.4.0

COPY aas.json startup.sh consul_info_sm.json /opt
RUN chmod +x /opt/startup.sh && \
    apt update && apt install -y curl jq

ENTRYPOINT ["bash", "-c", "/opt/startup.sh"]