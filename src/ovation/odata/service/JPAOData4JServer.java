package ovation.odata.service;


public class JPAOData4JServer {

    public static void main(String[] args) {
        String endpointUri = "http://localhost:8886/OvoData.svc/";
        
        // this example assumes you have an appropriate persistence.xml containing a valid persistence unit definition 
        // (in this case named NorthwindServiceEclipseLink) mapping your jpa entity classes, etc
        
        // create a JPAProducer by giving it a EntityManagerFactory
        String persistenceUnitName = ""; //"NorthwindService"+OData4jTestSuite.JPA_PROVIDER.caption;
        String namespace = ""; //"Northwind";
/*        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        
        JPAProducer producer = new JPAProducer(emf, namespace,50);
//        NorthwindTestUtils.fillDatabase(emf);
        
        // register the producer as the static instance, then launch the http server
        ODataProducerProvider.setInstance(producer);
        
        // start the server and wait for CR to quit
        OData4JServerHelper.hostODataServer(endpointUri);
*/        
    }
}