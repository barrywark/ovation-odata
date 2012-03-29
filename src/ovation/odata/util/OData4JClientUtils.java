package ovation.odata.util;

import java.util.List;

import org.core4j.Enumerable;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmAssociationSet;
import org.odata4j.edm.EdmComplexType;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmFunctionImport;
import org.odata4j.edm.EdmFunctionParameter;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmSimpleType;

public class OData4JClientUtils {

    public static void report(String msg) {
        System.out.println(msg);
    }

    public static void report(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
    
    public static void reportEntity(String caption, OEntity entity){
        report(caption);
        for(OProperty<?> p : entity.getProperties()){
                Object v = p.getValue();
                if (p.getType().equals(EdmSimpleType.BINARY))
                        v = org.odata4j.repack.org.apache.commons.codec.binary.Base64.encodeBase64String((byte[])v).trim();
            report("  %s: %s",p.getName(),v);
        }
    }
    public static int reportEntities(ODataConsumer c, String entitySet, int limit){
        report("entitySet: " + entitySet);
        Enumerable<OEntity> entities =  c.getEntities(entitySet).execute().take(limit);
        return reportEntities(entitySet, entities);
    }
    public static int reportEntities(String entitySet, Enumerable<OEntity> entities){
        
        int count = 0;
        
        for(OEntity e :entities){
            reportEntity(entitySet + " entity" + count,e);
            count++;
        }
        report("total count: %s \n\n" , count);
        
        return count;
    }
/* FIXME 0.6    
    private static void reportProperties(Enumerable<EdmProperty> properties){
        for(EdmProperty property : properties){
            String p = String.format("Property Name=%s Type=%s Nullable=%s",property.getName(),property.getType(),property.isNullable());
            if (property.getMaxLength() != null)
                p = p + " MaxLength="+ property.getMaxLength();
            if (property.getUnicode() != null)
                p = p + " Unicode="+ property.getUnicode();
            if (property.getFixedLength() != null)
                p = p + " FixedLength="+ property.getFixedLength();   
            if (property.getStoreGeneratedPattern() != null)
                p = p + " StoreGeneratedPattern="+ property.getStoreGeneratedPattern();  
            if (property.getFcTargetPath() != null)
                p = p + " TargetPath="+ property.getFcTargetPath();
            if (property.getFcContentKind() != null)
                p = p + " ContentKind="+ property.getFcContentKind();
            if (property.getFcKeepInContent() != null)
                p = p + " KeepInContent="+ property.getFcKeepInContent();
            if (property.getFcEpmContentKind() != null)
                p = p + " EpmContentKind="+ property.getFcEpmContentKind();
            if (property.getFcEpmKeepInContent() != null)
                p = p + " EpmKeepInContent="+ property.getFcEpmKeepInContent();
            report("    "+ p);
        }
    }
*/    
    private static void reportProperties(List<EdmProperty> properties){
        for(EdmProperty property : properties){
            String p = String.format("Property Name=%s Type=%s Nullable=%s",property.name,property.type,property.nullable);
            if (property.maxLength != null)
                p = p + " MaxLength="+ property.maxLength;
            if (property.unicode != null)
                p = p + " Unicode="+ property.unicode;
            if (property.fixedLength != null)
                p = p + " FixedLength="+ property.fixedLength;   
            
            if (property.storeGeneratedPattern != null)
                p = p + " StoreGeneratedPattern="+ property.storeGeneratedPattern;  
            
            if (property.fcTargetPath != null)
                p = p + " TargetPath="+ property.fcTargetPath;
            if (property.fcContentKind != null)
                p = p + " ContentKind="+ property.fcContentKind;
            if (property.fcKeepInContent != null)
                p = p + " KeepInContent="+ property.fcKeepInContent;
            if (property.fcEpmContentKind != null)
                p = p + " EpmContentKind="+ property.fcEpmContentKind;
            if (property.fcEpmKeepInContent != null)
                p = p + " EpmKeepInContent="+ property.fcEpmKeepInContent;
            report("    "+ p);
        }
    }    
    
    public static void reportMetadata(EdmDataServices services){
        
        for(EdmSchema schema : services.getSchemas()){
            report("Schema Namespace=%s",schema.namespace);
            
            for(EdmEntityType et : schema.entityTypes){
                String ets = String.format("  EntityType Name=%s",et.name);
                if (et.hasStream != null)
                    ets = ets + " HasStream="+et.hasStream;
                report(ets);
                
                for(String key : et.getKeys()){
                    report("    Key PropertyRef Name=%s",key);
                }
                
                reportProperties(et.properties);
                for(EdmNavigationProperty np : et.getNavigationProperties()){
                    report("    NavigationProperty Name=%s Relationship=%s FromRole=%s ToRole=%s",
                            np.name,np.relationship.getFQNamespaceName(),np.fromRole.role,np.toRole.role);
                }
                 
            }
            for(EdmComplexType ct : schema.complexTypes){
                report("  ComplexType Name=%s",ct.name);
                
                reportProperties(ct.properties);
               
            }
            for(EdmAssociation assoc : schema.associations){
                report("  Association Name=%s",assoc.name);
                report("    End Role=%s Type=%s Multiplicity=%s",assoc.end1.role,assoc.end1.type.getFullyQualifiedTypeName(),assoc.end1.multiplicity);
                report("    End Role=%s Type=%s Multiplicity=%s",assoc.end2.role,assoc.end2.type.getFullyQualifiedTypeName(),assoc.end2.multiplicity);
            }
            for(EdmEntityContainer ec : schema.entityContainers){
                report("  EntityContainer Name=%s IsDefault=%s LazyLoadingEnabled=%s",ec.name,ec.isDefault,ec.lazyLoadingEnabled);
                
                for(EdmEntitySet ees : ec.entitySets){
                    report("    EntitySet Name=%s EntityType=%s",ees.name,ees.type.getFullyQualifiedTypeName());
                }
                
                for(EdmAssociationSet eas : ec.associationSets){
                    report("    AssociationSet Name=%s Association=%s",eas.name,eas.association.getFQNamespaceName());
                    report("      End Role=%s EntitySet=%s",eas.end1.role.role,eas.end1.entitySet.name);
                    report("      End Role=%s EntitySet=%s",eas.end2.role.role,eas.end2.entitySet.name);
                }
                
                for(EdmFunctionImport efi : ec.functionImports){
                    report("    FunctionImport Name=%s EntitySet=%s ReturnType=%s HttpMethod=%s",
                            efi.name,efi.entitySet==null?null:efi.entitySet.name,efi.returnType,efi.httpMethod);
                    for(EdmFunctionParameter efp : efi.parameters){
                        report("      Parameter Name=%s Type=%s Mode=%s",efp.name,efp.type,efp.mode);
                    }
                }
            }
        }
    }
/* FIXME 0.6    
    public static void reportMetadata(EdmDataServices services){
        
        for(EdmSchema schema : services.getSchemas()){
            report("Schema Namespace=%s",schema.getNamespace());
            
            for(EdmEntityType et : schema.getEntityTypes()){
                String ets = String.format("  EntityType Name=%s",et.getName());
                if (et.getHasStream() != null)
                    ets = ets + " HasStream="+et.getHasStream();
                report(ets);
                
                for(String key : et.getKeys()){
                    report("    Key PropertyRef Name=%s",key);
                }
                
                reportProperties(et.getProperties());
                for(EdmNavigationProperty np : et.getNavigationProperties()){
                    report("    NavigationProperty Name=%s Relationship=%s FromRole=%s ToRole=%s",
                            np.getName(),np.getRelationship().getFQNamespaceName(),np.getFromRole().getRole(),np.getToRole().getRole());
                }
                 
            }
            for(EdmComplexType ct : schema.getComplexTypes()){
                report("  ComplexType Name=%s",ct.getName());
                
                reportProperties(ct.getProperties());
               
            }
            for(EdmAssociation assoc : schema.getAssociations()){
                report("  Association Name=%s",assoc.getName());
                report("    End Role=%s Type=%s Multiplicity=%s",assoc.getEnd1().getRole(),assoc.getEnd1().getType().getFullyQualifiedTypeName(),assoc.getEnd1().getMultiplicity());
                report("    End Role=%s Type=%s Multiplicity=%s",assoc.getEnd2().getRole(),assoc.getEnd2().getType().getFullyQualifiedTypeName(),assoc.getEnd2().getMultiplicity());
            }
            for(EdmEntityContainer ec : schema.getEntityContainers()){
                report("  EntityContainer Name=%s IsDefault=%s LazyLoadingEnabled=%s",ec.getName(),ec.isDefault(),ec.getLazyLoadingEnabled());
                
                for(EdmEntitySet ees : ec.getEntitySets()){
                    report("    EntitySet Name=%s EntityType=%s",ees.getName(),ees.getType().getFullyQualifiedTypeName());
                }
                
                for(EdmAssociationSet eas : ec.getAssociationSets()){
                    report("    AssociationSet Name=%s Association=%s",eas.getName(),eas.getAssociation().getFQNamespaceName());
                    report("      End Role=%s EntitySet=%s",eas.getEnd1().getRole().getRole(),eas.getEnd1().getEntitySet().getName());
                    report("      End Role=%s EntitySet=%s",eas.getEnd2().getRole().getRole(),eas.getEnd2().getEntitySet().getName());
                }
                
                for(EdmFunctionImport efi : ec.getFunctionImports()){
                    report("    FunctionImport Name=%s EntitySet=%s ReturnType=%s HttpMethod=%s",
                            efi.getName(),efi.getEntitySet()==null?null:efi.getEntitySet().getName(),efi.getReturnType(),efi.getHttpMethod());
                    for(EdmFunctionParameter efp : efi.getParameters()){
                        report("      Parameter Name=%s Type=%s Mode=%s",efp.getName(),efp.getType(),efp.getMode());
                    }
                }
            }
        }
    }
*/    
}
