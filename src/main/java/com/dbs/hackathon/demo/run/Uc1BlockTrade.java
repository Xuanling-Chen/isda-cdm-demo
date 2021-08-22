package com.dbs.hackathon.demo.run;

import com.dbs.hackathon.demo.utils.CDMUtils;
import org.isda.cdm.Event;
import org.isda.cdm.Execution;
import org.isda.cdm.ExecutionState;
import org.isda.cdm.PartyRoleEnum;
import org.isda.cdm.metafields.ReferenceWithMetaParty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.dbs.hackathon.demo.utils.ResourcesUtils.getObject;

public class Uc1BlockTrade {
    public static void main(String[] args) throws IOException {
        String fileName = "/Users/xuanling_chen/hackerthon/isda-cdm-demo/src/main/resources/data/uc1_result.csv";
        String header = "REFERENCE,TRADE_DATE,SETTLEMENT_DATE,CLIENT(ROLE),EXE_BROKER(ROLE),CTPY_BROKER(ROLE),BOND,QUANTITY,CURRENCY,AMOUNT,GROSS_PRICE,NET_PRICE,INTEREST";

        /*BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true));
        writer.write(header+"\n");
        writer.append(uc1Parser("data/UC1_Block_Trade_BT1.json"));
        writer.append(uc1Parser("data/UC1_Block_Trade_BT2.json"));
        writer.append(uc1Parser("data/UC1_Block_Trade_BT3.json"));
        writer.append(uc1Parser("data/UC1_Block_Trade_BT4.json"));
        writer.append(uc1Parser("data/UC1_Block_Trade_BT5.json"));
        writer.append(uc1Parser("data/UC1_Block_Trade_BT6.json"));
        writer.append(uc1Parser("data/UC1_Block_Trade_BT7.json"));
        writer.append(uc1Parser("data/UC1_Block_Trade_BT8.json"));
        writer.append(uc1Parser("data/UC1_Block_Trade_BT9.json"));
        writer.append(uc1Parser("data/UC1_Block_Trade_BT10.json"));
        writer.close();*/
        System.out.println(uc1Parser("data/UC1_Block_Trade_BT1.json"));
        System.out.println(extractUc1("data/UC1_Block_Trade_BT1.json"));
    }

    public static String extractUc1(String filePath){
        try {
            Event eventTemplate = getObject(Event.class, filePath);

            String reference = eventTemplate.getEventEffect().getEffectedExecution()
                    .stream().findFirst().map(r -> r.getGlobalReference()).get();
            ExecutionState state = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()).findFirst().get();
            String tradeDate = CDMUtils.extractTradeDate(state);
            String settlementDate = CDMUtils.extractSettlementDate(state);
            ReferenceWithMetaParty clientPartyRef = CDMUtils.extractParty(state,PartyRoleEnum.CLIENT);
            String clientName = CDMUtils.extractPartyName(eventTemplate.getParty(),clientPartyRef);
            String role1 = CDMUtils.extractBuyerOrSellerRole(state,clientPartyRef);

            ReferenceWithMetaParty brokePartyRef = CDMUtils.extractParty(state,PartyRoleEnum.EXECUTING_ENTITY);
            String brokerName = CDMUtils.extractPartyName(eventTemplate.getParty(),brokePartyRef);
            String role2 = CDMUtils.extractBuyerOrSellerRole(state,brokePartyRef);

            ReferenceWithMetaParty ctptPartyRef = CDMUtils.extractParty(state,PartyRoleEnum.COUNTERPARTY);
            String ctpyBrokerName = CDMUtils.extractPartyName(eventTemplate.getParty(),ctptPartyRef);
            String role3 = CDMUtils.extractBuyerOrSellerRole(state,ctptPartyRef);


            String bond = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getProduct().getSecurity().getBond().getProductIdentifier().getIdentifier()
                    .stream().findFirst().get().getValue()).findFirst().get();

            String quantities = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getQuantity().getAmount().toString()).findFirst().get();

            String currency = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getSettlementTerms().getSettlementAmount().getCurrency().getValue()).findFirst().get();

            String amount = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getSettlementTerms().getSettlementAmount().getAmount().toString()).findFirst().get();

            String grossPrice = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getPrice().getGrossPrice().getAmount().toString()).findFirst().get();

            String netPrice = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getPrice().getNetPrice().getAmount().toString()).findFirst().get();

            String interest = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getPrice().getAccruedInterest().toString()).findFirst().get();
            StringBuffer sb = new StringBuffer();
            sb.append(reference).append(",")
                    .append(tradeDate).append(",")
                    .append(settlementDate).append(",")
                    .append(clientName+"("+role1+")").append(",")
                    .append(brokerName+"("+role2+")").append(",")
                    .append(ctpyBrokerName+"("+role3+")").append(",")
                    .append(bond).append(",")
                    .append(quantities).append(",")
                    .append(currency).append(",")
                    .append(amount).append(",")
                    .append(grossPrice).append(",")
                    .append(netPrice).append(",")
                    .append(interest).append("\n");

            System.out.println(sb);

        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String uc1Parser(String filePath) {

        try {
            Event eventTemplate =
                    getObject(Event.class, filePath);

            String reference = eventTemplate.getEventEffect().getEffectedExecution()
                    .stream().findFirst().map(r -> r.getGlobalReference()).get();

//            System.out.println("Assert to be xxdy/Zsa8dH/GeGisnjJhdqR8cGAuJEU2idvHFlCsuo= <==> " + reference);

            String tradeDate = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getTradeDate().getValue().getYear() + "-" + e.getAfter()
                    .getExecution().getTradeDate().getValue().getMonth() + "-" + e.getAfter()
                    .getExecution().getTradeDate().getValue().getDay()
            ).findFirst().get();

//            System.out.println("Assert to be 2019-10-16 <==> " + tradeDate);

            String settlementDate = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getSettlementTerms().getSettlementDate().getAdjustableDate().getUnadjustedDate().getYear() + "-" + e.getAfter()
                    .getExecution().getSettlementTerms().getSettlementDate().getAdjustableDate().getUnadjustedDate().getMonth() + "-" + e.getAfter()
                    .getExecution().getSettlementTerms().getSettlementDate().getAdjustableDate().getUnadjustedDate().getDay()
            ).findFirst().get();

//            System.out.println("Assert to be 2019-10-17 <==> " + settlementDate);

            String clientRef = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter().getExecution()
                    .getPartyRole().stream().filter(pr -> pr.getRole().equals(PartyRoleEnum.CLIENT))
                    .findFirst().get().getPartyReference().getGlobalReference()).findFirst().get();
//            System.out.println("Assert to be xIBwSYskqfYuo71OylXWHFH1u5lbAcAdAnM4LcNheBg <==> " + clientRef);
            String clientName = eventTemplate.getParty().stream().filter(p -> p.getMeta()
                    .getGlobalKey().equals(clientRef)).map(p -> p.getName()).map(n -> n.getValue()).findFirst().get();

//            System.out.println("Assert To Be Cline1 <==> " + clientName);

            String role1 = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter().getExecution()
                    .getPartyRole().stream().filter(pr -> pr.getPartyReference().getGlobalReference().equals(clientRef))
                    .filter(pr -> !pr.getRole().equals(PartyRoleEnum.CLIENT))
                    .findFirst().get().getRole().name()).findFirst().get();
//            System.out.println("Assert To Be BUYER <==> " + role1);

            String exeBrokerRef = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter().getExecution()
                    .getPartyRole().stream().filter(pr -> pr.getRole().equals(PartyRoleEnum.EXECUTING_ENTITY))
                    .findFirst().get().getPartyReference().getGlobalReference()).findFirst().get();

//            System.out.println("Assert To Be 3vqQOOnXah+v+Cwkdh/hSyDP7iD6lLGqRDW/500GvjU= <==> " + exeBrokerRef);

            String brokerName = eventTemplate.getParty().stream().filter(p -> p.getMeta()
                    .getGlobalKey().equals(exeBrokerRef)).map(p -> p.getName()).map(n -> n.getValue()).findFirst().get();

//            System.out.println("Assert To Be Broker1 <==> " + brokerName);

//            String role2 = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter().getExecution()
//                    .getPartyRole().stream().filter(pr -> pr.getPartyReference().getGlobalReference().equals(exeBrokerRef))
//                    .filter(pr -> !pr.getRole().equals(PartyRoleEnum.EXECUTING_ENTITY))
//                    .findFirst().get().getRole().name()).findFirst().get();

            String role2 = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter().getExecution()
                    .getPartyRole().stream().filter(pr -> pr.getPartyReference().getGlobalReference().equals(exeBrokerRef))
                    .filter(pr -> pr.getRole().equals(PartyRoleEnum.BUYER) || pr.getRole().equals(PartyRoleEnum.SELLER))
                    .findFirst().get().getRole().name()).findFirst().get();
            System.out.println("Assert To Be BUYER <==> " + role2);


            String ctptBrokerRef = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter().getExecution()
                    .getPartyRole().stream().filter(pr -> pr.getRole().equals(PartyRoleEnum.COUNTERPARTY))
                    .findFirst().get().getPartyReference().getGlobalReference()).findFirst().get();

//            System.out.println("Assert To Be NKbmTRySI+MwEy+qqV0+romOrBu0GsWdqogMXGtzSVM= <==> " + ctptBrokerRef);

            String ctpyBrokerName = eventTemplate.getParty().stream().filter(p -> p.getMeta()
                    .getGlobalKey().equals(ctptBrokerRef)).map(p -> p.getName()).map(n -> n.getValue()).findFirst().get();

//            System.out.println("Assert To Be Broker2 <==> " + ctpyBrokerName);

            String role3 = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter().getExecution()
                    .getPartyRole().stream().filter(pr -> pr.getPartyReference().getGlobalReference().equals(ctptBrokerRef))
                    .filter(pr -> pr.getRole().equals(PartyRoleEnum.BUYER) || pr.getRole().equals(PartyRoleEnum.SELLER))
                    .findFirst().get().getRole().name()).findFirst().get();

            System.out.println("Assert To Be SELLER <==> " + role3);

            String bond = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getProduct().getSecurity().getBond().getProductIdentifier().getIdentifier()
                    .stream().findFirst().get().getValue()).findFirst().get();

//            System.out.println("Assert To Be DH0371475458 <==> " + bond);

            String quantities = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getQuantity().getAmount().toString()).findFirst().get();

//            System.out.println("Assert To Be 800000.00 <==> " + quantities);

            String currency = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getSettlementTerms().getSettlementAmount().getCurrency().getValue()).findFirst().get();

//            System.out.println("Assert To Be USD <==> " + currency);


            String amount = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getSettlementTerms().getSettlementAmount().getAmount().toString()).findFirst().get();

//            System.out.println("Assert To Be 786720.00 <==> " + amount);

            String grossPrice = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getPrice().getGrossPrice().getAmount().toString()).findFirst().get();

//            System.out.println("Assert To Be 96.9700 <==> " + grossPrice);

            String netPrice = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getPrice().getNetPrice().getAmount().toString()).findFirst().get();

//            System.out.println("Assert To Be 98.3400 <==> " + netPrice);

            String interest = eventTemplate.getPrimitive().getExecution().stream().map(e -> e.getAfter()
                    .getExecution().getPrice().getAccruedInterest().toString()).findFirst().get();

//            System.out.println("Assert To Be 1.3700 <==> " + interest);

            StringBuffer sb = new StringBuffer();
            sb.append(reference).append(",")
            .append(tradeDate).append(",")
            .append(settlementDate).append(",")
                    .append(clientName+"("+role1+")").append(",")
                    .append(brokerName+"("+role2+")").append(",")
                    .append(ctpyBrokerName+"("+role3+")").append(",")
                    .append(bond).append(",")
                    .append(quantities).append(",")
                    .append(currency).append(",")
                    .append(amount).append(",")
                    .append(grossPrice).append(",")
                    .append(netPrice).append(",")
                    .append(interest).append("\n");

            System.out.println(sb);
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
