import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class MyNeo4jDemo {

    public static void main(String[] args) {
        MyNeo4jDemo mycypherdemo=new MyNeo4jDemo();
        String inputString = """
                        MERGE(cc_payment___uai___from: uai {id_value:'+60125885869'})
                        on create SET cc_payment___uai___from.id_type= 'phone_number',cc_payment___uai___from.id_value= '+60125885869'
                        on match set cc_payment___uai___from.id_type= 'phone_number',cc_payment___uai___from.id_value= '+60125885869';
                               
                        MERGE(cc_payment___uai___to: uai {id_value:'merchant001'})
                        on create SET cc_payment___uai___to.id_type= 'merchant_id',cc_payment___uai___to.id_value= 'merchant001'
                        on match SET cc_payment___uai___to.id_type= 'merchant_id',cc_payment___uai___to.id_value= 'merchant001';
                                
                        MERGE(cc_payment___device: device {device_fingerprint:'device001'})
                        on create SET cc_payment___device.device_fingerprint= 'device001',cc_payment___device.device_type= 'ipad',cc_payment___device.ip_geolocation= 'geoloc001'
                        on match set cc_payment___device.device_fingerprint= 'device001',cc_payment___device.device_type= 'ipad',cc_payment___device.ip_geolocation= 'geoloc001';
                                
                        MERGE(cc_payment___address___shipping: address {address:'taman melati'})
                        on create SET cc_payment___address___shipping.address= 'taman melati'
                        on match  set cc_payment___address___shipping.address= 'taman melati';
                                
                        MERGE(cc_payment___address___billing: address {address:'taman jaya'})
                        on create SET cc_payment___address___billing.address= 'taman jaya'
                        on match set cc_payment___address___billing.address= 'taman jaya';
                                
                        MERGE(cc_payment___payment_instrument: payment_instrument {instrument_id:'4012888888881881'})
                        on create SET cc_payment___payment_instrument.instrument_type= 'credit_card_no',cc_payment___payment_instrument.instrument_id= '4012888888881881'
                        on match set cc_payment___payment_instrument.instrument_type= 'credit_card_no',cc_payment___payment_instrument.instrument_id= '4012888888881881';
                                
                        MATCH (cc_payment___uai___from: uai {id_value:'+60125885869'}),
                        (cc_payment___address___shipping: address {address:'taman melati'})
                        MERGE (cc_payment___uai___from)-[edge1:shipping_address {}]->(cc_payment___address___shipping)
                          ON CREATE SET edge1.__frequency = 1 ON MATCH SET edge1.__frequency = edge1.__frequency + 1;
                                
                        MATCH (cc_payment___uai___from: uai {id_value:'+60125885869'}), (cc_payment___address___billing: address {address:'taman jaya'})
                        MERGE (cc_payment___uai___from)-[edge2:billing_address {}]->(cc_payment___address___billing)
                          ON CREATE SET edge2.__frequency = 1 ON MATCH SET edge2.__frequency = edge2.__frequency + 1;
                                
                        MATCH (cc_payment___uai___from: uai {id_value:'+60125885869'}), (cc_payment___device: device {device_fingerprint:'device001'})
                        MERGE (cc_payment___uai___from)-[edge3:uses_device {}]->(cc_payment___device)
                          ON CREATE SET edge3.__frequency = 1 ON MATCH SET edge3.__frequency = edge3.__frequency + 1;
                                
                        MATCH (cc_payment___uai___from: uai {id_value:'+60125885869'}), (cc_payment___payment_instrument: payment_instrument {instrument_id:'4012888888881881'})
                        MERGE (cc_payment___uai___from)-[edge4:payment_from {transaction_id:'trx123'}]->(cc_payment___payment_instrument)
                        on create SET edge4.transaction_id= 'trx123',edge4.amount= 500000,edge4.datetime= datetime('2022-01-03T10:20:58Z'),edge4.transaction_type= 'cc_payment'
                        on match set edge4.transaction_id= 'trx123',edge4.amount= 500000,edge4.datetime= datetime('2022-01-03T10:20:58Z'),edge4.transaction_type= 'cc_payment';
                                
                        MATCH (cc_payment___payment_instrument: payment_instrument {instrument_id:'4012888888881881'}), (cc_payment___uai___to: uai {id_value:'merchant001'})
                        MERGE (cc_payment___payment_instrument)-[edge5:payment_to {transaction_id:'trx123'}]->(cc_payment___uai___to)
                        on create SET edge5.transaction_id= 'trx123',edge5.amount= 500000,edge5.datetime= datetime('2022-01-03T10:20:58Z'),edge5.transaction_type= 'cc_payment'
                        on match set edge5.transaction_id= 'trx123',edge5.amount= 500000,edge5.datetime= datetime('2022-01-03T10:20:58Z'),edge5.transaction_type= 'cc_payment'
                """;


        List<String> cypher_List = new ArrayList<String>(Arrays.asList(inputString.split(";")));
        mycypherdemo.runTest(cypher_List);
    }
    
    public void runTest (List<String> cypher_List)
    {
        String uri_prefix="bolt://";
        String uri_postfix=":7687";
        String uri_ipaddr_default="localhost";
        String my_uri=uri_prefix+uri_ipaddr_default+uri_postfix;

        try (Neo4jClient client = new Neo4jClient(my_uri, "neo4j", "Ne04j!",  100)) {
            client.runCypherParallel(100,cypher_List,"gbg");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
