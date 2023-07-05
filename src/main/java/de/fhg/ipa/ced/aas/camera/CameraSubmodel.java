package de.fhg.ipa.ced.aas.camera;

import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.Blob;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.ReferenceElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;

public class CameraSubmodel extends Submodel {
    public static final String SM_ID_SHORT = "Camera";
    public static final IIdentifier SM_ID = new CustomId("fabos.submodel.device.camera");
    public static final String OPERATION_ID_SHORT = "snapshot";
    public static final String PROPERTY_ID_SHORT = "image";
    public static final String SEMANTIC_ID_REFERENCE_ELEMENT_VALUE = "fabos_camera_bp";

    IImageGrabber imageGrabber;
    public CameraSubmodel(IImageGrabber imageGrabber) {
        super(SM_ID_SHORT, SM_ID);

        setLastSnapshot("");
        setLastSnapshotTimestamp("");

        Operation snapshot = new Operation(OPERATION_ID_SHORT);
        snapshot.setInvokable(this::snapshot);
        snapshot.setOutputVariables(createOutputVariables());
        addSubmodelElement(snapshot);

        super.setSemanticId(new Reference(
                new Key(KeyElements.REFERENCEELEMENT, false, SEMANTIC_ID_REFERENCE_ELEMENT_VALUE, KeyType.CUSTOM)
        ));
        this.imageGrabber = imageGrabber;
    }

    public void setLastSnapshot(Blob lastSnapshot) {
        addSubmodelElement(lastSnapshot);
    }

    public void setLastSnapshot(String base64JPG) {
        Blob lastSnapshot = new Blob("lastSnapshot", "image/jpeg");
        lastSnapshot.setValue(base64JPG);
        setLastSnapshot(lastSnapshot);
    }

    public void setLastSnapshotTimestamp(Property lastSnapshotTimestamp) {
        addSubmodelElement(lastSnapshotTimestamp);
    }

    public void setLastSnapshotTimestamp(String timestamp) {
        Property timestampProp = new Property("lastSnapshotTimestamp", ValueType.DateTimeStamp);
        timestampProp.setValue(timestamp);
        setLastSnapshotTimestamp(timestampProp);
    }

    public void setLastSnapshotTimestamp(ZonedDateTime timestamp) {
        setLastSnapshotTimestamp(timestamp.toString());
    }
    
    private Collection<OperationVariable> createOutputVariables() {
        Blob image = new Blob(PROPERTY_ID_SHORT, "image/jpeg");
        image.setKind(ModelingKind.TEMPLATE);
        OperationVariable imageOpVar = new OperationVariable(image);
        return Collections.singleton(imageOpVar);
    }

    private String snapshot() {
        String base64Frame = imageGrabber.getBase64Frame();
        setLastSnapshot(base64Frame);
        setLastSnapshotTimestamp(ZonedDateTime.now());
        return base64Frame;
    }

}
