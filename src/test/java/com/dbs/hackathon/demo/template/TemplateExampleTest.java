package com.dbs.hackathon.demo.template;

//import cdm.event.common.BusinessEvent;
//import cdm.event.common.TradeState;
//import cdm.product.template.ContractualProduct;
import com.fasterxml.jackson.core.JsonProcessingException;
//import com.regnosys.rosetta.common.hashing.GlobalKeyProcessStep;
import com.regnosys.rosetta.common.hashing.NonNullHashCollector;
import com.regnosys.rosetta.common.hashing.ReKeyProcessStep;
//import com.regnosys.rosetta.common.merging.MergeTemplateProcessStep;
//import com.regnosys.rosetta.common.merging.SimpleMerger;
//import com.regnosys.rosetta.common.merging.SimpleSplitter;
import com.regnosys.rosetta.common.serialisation.RosettaObjectMapper;
//import com.regnosys.rosetta.common.util.RosettaModelObjectSupplier;
//import com.rosetta.model.lib.RosettaModelObject;
import com.rosetta.model.lib.RosettaModelObjectBuilder;
import com.rosetta.model.lib.process.PostProcessStep;
import org.isda.cdm.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Optional.of;
import static com.dbs.hackathon.demo.utils.ResourcesUtils.getObject;
//import static org.junit.jupiter.api.Assertions.assertEquals;
///**
// * Test to demonstrate how to use data templates in the CDM.
// *
// * Regenerate sample data with com.regnosys.granite.ingestor.template.GenerateTemplateExampleJsonWriter.
// *
// * @see MergeTemplateProcessStep
// * @see RosettaModelObjectSupplier
// * @see com.rosetta.model.lib.process.BuilderMerger
// * @see SimpleMerger
// * @see SimpleSplitter
// */
public class TemplateExampleTest {

//	private RosettaModelObjectSupplier templateSupplier;
	private Consumer<RosettaModelObjectBuilder> reKeyPostProcessor;

	@Test
	void createModel() throws IOException {
		// ContractualProduct template object
		Event businessEventTemplate =
				getObject(Event.class, "data/UC1_Block_Trade_BT1.json");
		System.out.println(businessEventTemplate.getEventEffect().getEffectedExecution().get(0).getGlobalReference());
		// Simple implementation that returns the contractual product template based on type and global key
//		templateSupplier = new RosettaModelObjectSupplier() {
//			@Override
//			public <T extends RosettaModelObject> Optional<T> get(Class<T> clazz, String globalKey) {
//				if (ContractualProduct.class.isAssignableFrom(clazz) && globalKey.equals(businessEventTemplate.getMeta().getGlobalKey())) {
//					return of((T) businessEventTemplate);
//				}
//				return Optional.empty();
//			}
//		};

		// Post-processors to re-generate key based on external keys which is injected into the merger / splitter.
		// Real implementations may or may not need to post-process the result builder, depending on their approach to creating ids / keys.
//		GlobalKeyProcessStep globalKeyProcessStep = new GlobalKeyProcessStep(NonNullHashCollector::new);
//		List<PostProcessStep> postProcessors = Arrays.asList(globalKeyProcessStep, new ReKeyProcessStep(globalKeyProcessStep));
//		reKeyPostProcessor = (builder) -> postProcessors.forEach(p -> p.runProcessStep(TradeState.class, builder));
	}

	/**
	 * The input is a TradeState that contains a partially populated ContractualProduct with a reference to a template, and following a template
	 * merge, the output is a fully populated valid TradeState object.
	 *
	 * The processor MergeTemplateProcessStep traverses through the TradeState object and when it finds a template reference, it gets the template
	 * object using the RosettaModelObjectSupplier, and merges it into the TradeState using the SimpleMerger to create a fully populated valid object.
	 *
	 * Input file: template/trade-state-unmerged.json
	 * Template file: template/contractual-product-template.json
	 * Output file: template/trade-state-merged.json
	 *
	 * @throws IOException if json file look up fails.
	 */
//	@Disabled
//	@Test
//	void shouldMergeContractualProductTemplateIntoContract() throws IOException {
//		TradeState.TradeStateBuilder builder = getObject(TradeState.class, "com/dbs/hackathon/demo/template/trade-state-unmerged.json").toBuilder();
//
//		new MergeTemplateProcessStep(new SimpleMerger(), templateSupplier, reKeyPostProcessor).runProcessStep(TradeState.class, builder);
//
//		TradeState merged = builder.build();
//		TradeState expected = getObject(TradeState.class, "com/dbs/hackathon/demo/template/trade-state-merged.json");
//		assertEquals(toJson(expected), toJson(merged));
//	}

	/**
	 * The input is a TradeState fully populated ContractualProduct, and following a template split, the output is a partially populated TradeState
	 * object with a reference to a template.
	 *
	 * The processor MergeTemplateProcessStep traverses through the TradeState object and when it finds a template reference, it gets the template
	 * object using the RosettaModelObjectSupplier, and subtracts the template from the TradeState using the SimpleSplitter to create a partially
	 * populated TradeState object.
	 *
	 * Input file: template/trade-state-merged.json
	 * Template file: template/contractual-product-template.json
	 * Output file: template/trade-state-unmerged.json
	 *
	 * @throws IOException if json file look up fails.
	 */
//	@Test
//	void shouldSplitContractualProductTemplateAndContract() throws IOException {
//		TradeState.TradeStateBuilder builder = getObject(TradeState.class, "com/dbs/hackathon/demo/template/trade-state-merged.json").toBuilder();
//
//		new MergeTemplateProcessStep(new SimpleSplitter(), templateSupplier, reKeyPostProcessor).runProcessStep(TradeState.class, builder);
//
//		TradeState unmerged = builder.build();
//		TradeState expected = getObject(TradeState.class, "com/dbs/hackathon/demo/template/trade-state-unmerged.json");
//		assertEquals(toJson(expected), toJson(unmerged));
//	}

	/**
	 * Diff json rather than the object because it's more readable.
	 */
	private String toJson(Object object) throws JsonProcessingException {
		return RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
	}
}
