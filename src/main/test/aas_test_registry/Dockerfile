FROM eclipsebasyx/aas-registry:1.4.0

COPY aas.json startup.sh /opt
RUN chmod +x /opt/startup.sh && \
    apt update && apt install -y curl jq

ENTRYPOINT ["bash", "-c", "/opt/startup.sh"]