package com.dbs.hackathon.demo.run;


import com.dbs.hackathon.demo.exceptions.AllocationException;
import com.dbs.hackathon.demo.utils.CDMUtils;
import com.dbs.hackathon.demo.utils.ValidateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.regnosys.rosetta.common.serialisation.RosettaObjectMapper;
import org.isda.cdm.*;
import org.isda.cdm.metafields.ReferenceWithMetaParty;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.dbs.hackathon.demo.utils.ResourcesUtils.getObject;
public class Uc2AllocationTrade {

    public static void main(String[] args) {
        String fileName = "data/UC2_Allocation_Trade_AT1_failed.json";

        allocationTrade(fileName);
    }




    public static void allocationTrade(String fileName) {
        String header = "ALC_REFERENCE;BLK_REFERENCE;TRADE_DATE;SETTLEMENT_DATE;CLIENT(ROLE);EXE_BROKER(ROLE);CTPY_BROKER(ROLE);BOND;ALC_QUANTITY;BLK_QUANTITY;CURRENCY;ALC_AMOUNT;BLK_AMOUNT;GROSS_PRICE;NET_PRICE;INTEREST";

        System.out.println(header);
        try {
            Event event = getObject(Event.class, fileName);
            String effectedExecution1 = event.getEventEffect().getEffectedExecution().get(0).getGlobalReference();
            String effectedExecution2 = event.getEventEffect().getEffectedExecution().get(1).getGlobalReference();
            System.out.println(extractByExecution(event, effectedExecution1));
            System.out.println(extractByExecution(event, effectedExecution2));

            ValidateUtils.validateEvent(event);
            event.getPrimitive().getAllocation().forEach(a -> ValidateUtils.validateAllocation(a));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public static String extractByExecution(Event event, String effectedExecution){

            StringBuffer sb = new StringBuffer();
            try {

                AllocationPrimitive allocationPrimitive = event.getPrimitive().getAllocation().stream().findFirst().get();
                AllocationOutcome allocationOutcome = allocationPrimitive.getAfter();

                Execution allocationExecution = CDMUtils.extractAllocationExecutionState(allocationOutcome, effectedExecution);
                Execution originalExecution = CDMUtils.extractOriginalExecutionState(allocationOutcome);
                List<PartyRole> partyRoles1 = allocationExecution.getPartyRole();

                String originalKey = originalExecution.getMeta().getGlobalKey();
                String lineageKey = CDMUtils.extractLineageExecutionRef(event);
                String beforeKey = CDMUtils.extractBeforExecutionRef(allocationPrimitive);

                ValidateUtils.globalKeyValidation(originalKey, lineageKey, beforeKey);

                BigDecimal allocationTotalQuantities = CDMUtils.getAllocationTradeTotalQuantities(allocationPrimitive.getAfter().getAllocatedTrade());
                BigDecimal beforeQuantities = CDMUtils.extractBeforeQuantities(allocationPrimitive);
                BigDecimal originalBrokerQuantities = originalExecution.getQuantity().getAmount();

                ValidateUtils.quantitiesValidation(allocationTotalQuantities,beforeQuantities,originalBrokerQuantities);
                String exeBroker = CDMUtils.extractExecutionBroker(originalExecution);
                String tradeDate = CDMUtils.extractTradeDate(originalExecution);
                String settlementDate = CDMUtils.extractSettlementDate(originalExecution);

                ReferenceWithMetaParty clientPartyRef = CDMUtils.extractParty(partyRoles1, PartyRoleEnum.CLIENT);
                String clientName = CDMUtils.extractPartyName(event.getParty(), clientPartyRef);
                String role1 = CDMUtils.extractBuyerOrSellerRole(partyRoles1, clientPartyRef);

                ReferenceWithMetaParty brokePartyRef = CDMUtils.extractParty(partyRoles1, PartyRoleEnum.EXECUTING_ENTITY);
                String brokerName = CDMUtils.extractPartyName(event.getParty(), brokePartyRef);
                String role2 = CDMUtils.extractBuyerOrSellerRole(partyRoles1, brokePartyRef);

                ReferenceWithMetaParty ctptPartyRef = CDMUtils.extractParty(partyRoles1, PartyRoleEnum.COUNTERPARTY);
                String ctpyBrokerName = CDMUtils.extractPartyName(event.getParty(), ctptPartyRef);
                String role3 = CDMUtils.extractBuyerOrSellerRole(partyRoles1, ctptPartyRef);

                String productName = CDMUtils.extractProductName(allocationExecution);

                String alcQuantities = CDMUtils.extractQuantities(allocationExecution);
                String blkQuantities = CDMUtils.extractQuantities(originalExecution);
                String currency = CDMUtils.extractCurrency(originalExecution);

                String alcAmount = CDMUtils.extractAmount(allocationExecution);
                String blkAmount = CDMUtils.extractAmount(originalExecution);

                String grossPrice = CDMUtils.extractGrossPrice(originalExecution);

                String netPrice = CDMUtils.extractNetPrice(originalExecution);

                String interest = CDMUtils.extractInterest(originalExecution);

                sb.append(effectedExecution).append(",")
                        .append(exeBroker).append(",")
                        .append(tradeDate).append(",")
                        .append(settlementDate).append(",")
                        .append(clientName + "(" + role1 + ")").append(",")
                        .append(brokerName + "(" + role2 + ")").append(",")
                        .append(ctpyBrokerName + "(" + role3 + ")").append(",")
                        .append(productName).append(",")
                        .append(alcQuantities).append(",")
                        .append(blkQuantities).append(",")
                        .append(currency).append(",")
                        .append(alcAmount).append(",")
                        .append(blkAmount).append(",")
                        .append(grossPrice).append(",")
                        .append(netPrice).append(",")
                        .append(interest).append("\n");

//            System.out.println(sb);
            }catch (AllocationException e){
                System.out.println(e.getMessage());
            }
            return sb.toString();
        }
}
