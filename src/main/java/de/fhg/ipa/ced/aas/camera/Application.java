package de.fhg.ipa.ced.aas.camera;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.components.registry.configuration.RegistryBackend;
import org.eclipse.basyx.components.servlet.submodel.SubmodelServlet;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxHTTPServer;
public class Application {

	public static final String SERVER_URL = "http://localhost:4001/aasServer";
	public static final String REGISTRY_URL = "http://localhost:4000/registry";
	public static final IIdentifier CAMERA_AAS_ID = new CustomId("fabos.aas.device.camera");
	public static final String CAMERA_AAS_ID_SHORT = "Camera";
	public static final IIdentifier CAMERA_ASSET_ID = new CustomId("fabos.asset.device.camera");
	public static final String CAMERA_ASSET_ID_SHORT = "CameraAsset";
	public static void main(String[] args) {
		startRegistry();
		startAASServer();

		IAASRegistry registry = new AASRegistryProxy(REGISTRY_URL);
		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry);

		Asset asset = new Asset(CAMERA_ASSET_ID_SHORT, CAMERA_ASSET_ID, AssetKind.INSTANCE);
		AssetAdministrationShell aas = new AssetAdministrationShell(CAMERA_AAS_ID_SHORT, CAMERA_AAS_ID, asset);

		manager.createAAS(aas, SERVER_URL);

		IImageGrabber imageGrabber;
		if (true) {
			imageGrabber = new ImageGrabberFake();
		} else{
			imageGrabber = new ImageGrabber();
		}
		CameraSubmodel sm = new CameraSubmodel(imageGrabber);
		hostPreconfiguredSubmodel(sm);
		SubmodelDescriptor descriptor = new SubmodelDescriptor(sm, "http://localhost:4002/submodels/Camera");
		registry.register(aas.getIdentification(), descriptor);
	}

	private static void startRegistry() {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration(4000, "/registry");
		contextConfig.setAccessControlAllowOrigin("*");
		BaSyxRegistryConfiguration registryConfig = new BaSyxRegistryConfiguration(RegistryBackend.INMEMORY);

		RegistryComponent registry = new RegistryComponent(contextConfig, registryConfig);
		registry.startComponent();
	}

	private static void startAASServer() {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration(4001, "/aasServer");
		contextConfig.setAccessControlAllowOrigin("*");
		BaSyxAASServerConfiguration aasServerConfig = new BaSyxAASServerConfiguration();
		aasServerConfig.setRegistry(REGISTRY_URL);
		aasServerConfig.loadFromDefaultSource();

		AASServerComponent aasServer =  new AASServerComponent(contextConfig, aasServerConfig);
		aasServer.startComponent();
	}

	public static void hostPreconfiguredSubmodel(Submodel sm) {
		BaSyxContextConfiguration serverContext = new BaSyxContextConfiguration(4002, "/submodels");
		// Create the BaSyx context from the context configuration
		BaSyxContext context = serverContext.createBaSyxContext();

		// Create a new SubmodelServlet containing the passed submodel
		SubmodelServlet smServlet = new SubmodelServlet(sm);

		// Add the SubmodelServlet mapping to the context at the path "$SmIdShort"
		// Here, it possible to add further submodels using the same pattern
		context.addServletMapping("/" + sm.getIdShort() + "/*", smServlet);

		// Create and start a HTTP server with the context created above
		BaSyxHTTPServer preconfiguredSmServer = new BaSyxHTTPServer(context);
		preconfiguredSmServer.start();

	}


}
