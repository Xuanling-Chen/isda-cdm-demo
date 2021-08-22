package com.dbs.hackathon.demo.utils;

import org.isda.cdm.*;
import org.isda.cdm.metafields.ReferenceWithMetaParty;

import java.math.BigDecimal;
import java.util.List;

public class CDMUtils {


    public static ReferenceWithMetaParty extractParty(ExecutionState state, PartyRoleEnum partyRole){
        return state.getExecution().getPartyRole().stream().filter(pr -> pr.getRole().equals(partyRole))
                .findFirst().get().getPartyReference();
    }

    public static ReferenceWithMetaParty extractParty(List<PartyRole> partyRoles, PartyRoleEnum partyRole){
        return partyRoles.stream().filter(pr -> pr.getRole().equals(partyRole))
                .findFirst().get().getPartyReference();
    }

    public static ReferenceWithMetaParty extractParty(List<PartyRole> partyRoles, String globalKey){
        return partyRoles.stream().filter(pr -> pr.getPartyReference().getGlobalReference().equals(globalKey))
                .findFirst().get().getPartyReference();
    }
    public static String extractPartyName(List<Party> parties, ReferenceWithMetaParty reference){
        return parties.stream().filter(p -> p.getMeta()
                .getGlobalKey().equals(reference.getGlobalReference())).map(p -> p.getName()).map(n -> n.getValue()).findFirst().get();
    }

    public static String extractBuyerOrSellerRole(ExecutionState state, ReferenceWithMetaParty reference){
        return state.getExecution().getPartyRole().stream().filter(pr -> pr.getPartyReference().getGlobalReference().equals(reference.getGlobalReference()))
                .filter(pr -> pr.getRole().equals(PartyRoleEnum.BUYER) || pr.getRole().equals(PartyRoleEnum.SELLER))
                .findFirst().get().getRole().name();
    }

    public static String extractBuyerOrSellerRole(List<PartyRole> partyRoles, ReferenceWithMetaParty reference){
        return partyRoles.stream().filter(pr -> pr.getPartyReference().getGlobalReference().equals(reference.getGlobalReference()))
                .filter(pr -> pr.getRole().equals(PartyRoleEnum.BUYER) || pr.getRole().equals(PartyRoleEnum.SELLER))
                .findFirst().get().getRole().name();
    }
    public static String extractQuantities(ExecutionState state){
        return state.getExecution().getQuantity().getAmount().toString();
    }

    public static String extractQuantities(Execution execution){
        return execution.getQuantity().getAmount().toString();
    }

    public static String extractAmount(Execution execution){
        return execution.getSettlementTerms().getSettlementAmount().getAmount().toString();
    }

    public static String extractGrossPrice(Execution execution){
        return execution.getPrice().getGrossPrice().getAmount().toString();
    }

    public static String extractNetPrice(Execution execution){
        return execution.getPrice().getNetPrice().getAmount().toString();
    }

    public static String extractInterest(Execution execution){
        return execution.getPrice().getAccruedInterest().toString();
    }
    public static String extractCurrency(Execution execution){
       return execution.getSettlementTerms().getSettlementAmount().getCurrency().getValue();
    }
    public static String extractTradeDate(ExecutionState state){
        return state.getExecution().getTradeDate().getValue().getYear() + "-" + state
                .getExecution().getTradeDate().getValue().getMonth() + "-" + state
                .getExecution().getTradeDate().getValue().getDay();
    }

    public static String extractTradeDate(Execution execution){
        return execution.getTradeDate().getValue().getYear() + "-" + execution.getTradeDate().getValue().getMonth()
                + "-" + execution.getTradeDate().getValue().getDay();
    }
    public static String extractSettlementDate(ExecutionState state){
        return state.getExecution().getSettlementTerms().getSettlementDate().getAdjustableDate().getUnadjustedDate().getYear() + "-" + state
                .getExecution().getSettlementTerms().getSettlementDate().getAdjustableDate().getUnadjustedDate().getMonth() + "-" + state
                .getExecution().getSettlementTerms().getSettlementDate().getAdjustableDate().getUnadjustedDate().getDay();
    }

    public static String extractSettlementDate(Execution execution){
        return execution.getSettlementTerms().getSettlementDate().getAdjustableDate().getUnadjustedDate().getYear() + "-"
                + execution.getSettlementTerms().getSettlementDate().getAdjustableDate().getUnadjustedDate().getMonth() + "-"
                + execution.getSettlementTerms().getSettlementDate().getAdjustableDate().getUnadjustedDate().getDay();
    }

    public static Execution extractAllocationExecutionState(AllocationOutcome allocationOutcome, String effectedExecutionRef){
        return allocationOutcome.getAllocatedTrade().stream()
                .filter(at-> at.getExecution().getMeta().getGlobalKey().equals(effectedExecutionRef)).findFirst().get().getExecution();
    }

    public static Execution extractOriginalExecutionState(AllocationOutcome allocationOutcome){
        return allocationOutcome.getOriginalTrade().getExecution();
    }

    public static String extractLineageExecutionRef(Event event){
        return event.getLineage().getExecutionReference().stream().findFirst().get().getGlobalReference();
    }
    public static String extractBeforExecutionRef(AllocationPrimitive allocationPrimitive){
        return allocationPrimitive.getBefore().getExecution().getMeta().getGlobalKey();
    }
    public static String extractExecutionBroker(Execution execution){
        return execution.getPartyRole().stream().filter(pr -> pr.getRole().equals(PartyRoleEnum.EXECUTING_ENTITY))
                .findFirst().get().getPartyReference().getGlobalReference();
    }

    public static String extractProductName(Execution execution){
        return execution.getProduct().getSecurity().getBond().getProductIdentifier().getIdentifier()
                .stream().findFirst().get().getValue();
    }

    public static BigDecimal getAllocationTradeTotalQuantities(List<Trade> trades){
        BigDecimal total = BigDecimal.ZERO;
        for (Trade trade : trades) {
            total = total.add(trade.getExecution().getQuantity().getAmount());
        }
        return total;
    }

    public static BigDecimal extractBeforeQuantities(AllocationPrimitive primitive){
        return primitive.getBefore().getExecution().getQuantity().getAmount();
    }
}
