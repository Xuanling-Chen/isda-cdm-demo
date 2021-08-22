package com.dbs.hackathon.demo.utils;

import com.dbs.hackathon.demo.exceptions.AllocationException;
import com.rosetta.model.lib.path.RosettaPath;
import com.rosetta.model.lib.validation.ValidationResult;
import com.rosetta.model.lib.validation.Validator;
import org.isda.cdm.AllocationPrimitive;
import org.isda.cdm.Event;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ValidateUtils {

    public static boolean globalKeyValidation(String originalKey, String lineageKey,String beforeKey) throws AllocationException {
        if(originalKey.equals(lineageKey) && lineageKey.equals(beforeKey)){
            return true;
        }
       throw new AllocationException("Global key not matching......");
    }

    public static boolean quantitiesValidation(BigDecimal allocationQt,BigDecimal beforeQt, BigDecimal orignalQt) throws AllocationException{
        if(allocationQt.equals(beforeQt) && beforeQt.equals(orignalQt)){
            return true;
        }
      throw new AllocationException("Quantities not matching...[allocation total quantities]:" + allocationQt
              +" [original quantities]:"+orignalQt
              +" [before execution quantities]:" + beforeQt);
    }
    public static boolean validateEvent(Event event){
        List<Validator> validators = new ArrayList<>();
        validators.addAll(event.metaData().choiceRuleValidators());
        validators.addAll(event.metaData().dataRules());
        validators.add(event.metaData().validator());
        validators.stream().map(v -> v.validate(RosettaPath.valueOf("Event"),event)).forEach(System.out::println);
        return true;
    }

    public static boolean validateAllocation(AllocationPrimitive allocationPrimitive){
        List<Validator> validators = new ArrayList<>();
        validators.addAll(allocationPrimitive.metaData().choiceRuleValidators());
        validators.addAll(allocationPrimitive.metaData().dataRules());
        validators.add(allocationPrimitive.metaData().validator());
        validators.stream().map(v -> v.validate(RosettaPath.valueOf("AllocationPrimitive"),allocationPrimitive))
                .sorted(Comparator.comparing(ValidationResult::isSuccess, Boolean::compareTo))
                .forEach(System.out::println);
        return true;
    }
}
