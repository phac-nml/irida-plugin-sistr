package ca.corefacility.bioinformatics.irida.plugin.serotyping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.PipelineProvidedMetadataEntry;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.AnalysisSampleUpdater;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * {@link AnalysisSampleUpdater} for SISTR results to be written to
 * metadata of {@link Sample}s.
 */


public class SISTRSampleUpdater implements AnalysisSampleUpdater {
	private static final Logger logger = LoggerFactory.getLogger(SISTRSampleUpdater.class);
	
	private static final String SISTR_FILE = "sistr-predictions"; /*got to match the value in the irida_workflow.xml file <output name="serotyping report" fileName="report_ectyper.tsv"/>*/
	

	private MetadataTemplateService metadataTemplateService;
	private SampleService sampleService;
	private IridaWorkflowsService iridaWorkflowsService;

	// @formatter:off
	private Map<String, String> SISTR_FIELDS = ImmutableMap.<String,String>builder()
		.put("serovar", "SISTR serovar (overall)")
		.put("serovar_antigen","SISTR serovar (antigen)")
		.put("serovar_cgmlst", "SISTR serovar (cgMLST)")
		.put("mash_serovar", "SISTR serovar (MASH)")
		.put("serogroup", "SISTR Serogroup")
		.put("mash_subspecies", "SISTR MASH Subspecies")
		.put("o_antigen", "SISTR O-antigen")
		.put("h1", "SISTR H1")
		.put("h2", "SISTR H2")
		.put("cgmlst_subspecies", "SISTR cgMLST Subspecies")
		.put("cgmlst_ST", "SISTR cgMLST Sequence Type")
		.put("cgmlst_matching_alleles", "SISTR cgMLST Alleles Matching")
		.put("cgmlst_found_loci", "SISTR cgMLST Found Alleles")
		.put("cgmlst_genome_match","SISTR Genome Match (cgMLSR)")
		.put("mash_genome","SISTR Genome Match (MASH)")
		.put("mash_distance", "SISTR Genome Match Distance (MASH)")
		.put("qc_status", "SISTR QC Status")
		.put("qc_messages", "SISTR QC message(s)")
		.put("predicted_serovar_in_list", "SISTR reportable serovar?")
		.put("antigenic_formula", "SISTR antigenic formula")
		.build();
			// @formatter:on


	/**
	 * Builds a new {@link SISTRSampleUpdater} with the following information.
	 * 
	 * @param metadataTemplateService The {@link MetadatTemplateService}.
	 * @param sampleService           The {@link SampleService}.
	 * @param iridaWorkflowsService   The {@link IridaWorkflowsService}.
	 */
	public SISTRSampleUpdater(MetadataTemplateService metadataTemplateService, SampleService sampleService,
			IridaWorkflowsService iridaWorkflowsService) {
		this.metadataTemplateService = metadataTemplateService;
		this.sampleService = sampleService;
		this.iridaWorkflowsService = iridaWorkflowsService;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Collection<Sample> samples, AnalysisSubmission analysis) throws PostProcessingException {
		if (samples.size() != 1) {
			throw new PostProcessingException(
					"Expected one sample; got '" + samples.size() + "' for analysis [id=" + analysis.getId() + "]");
		}

		final Sample sample = samples.iterator().next();
		AnalysisOutputFile sistrFile = analysis.getAnalysis().getAnalysisOutputFile(SISTR_FILE);
		Path filePath = sistrFile.getFile();

		

		/*Read through SISTR_FILE json and update meta data fields*/
		Map<String, MetadataEntry> metadataEntries = new HashMap<>();
		try {

			IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(analysis.getWorkflowId());
			String workflowVersion = iridaWorkflow.getWorkflowDescription().getVersion();

			//Read the JSON file from SISTR output
			@SuppressWarnings("resource")
			String jsonFile = new Scanner(new BufferedReader(new FileReader(filePath.toFile()))).useDelimiter("\\Z").next();

			// map the results into a Map
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> sistrResults = mapper.readValue(jsonFile, new TypeReference<List<Map<String, Object>>>() {});

			if (sistrResults.size() > 0) {
				Map<String, Object> result = sistrResults.get(0);

				//loop through each of the requested fields and append workflow version and save the entries
				SISTR_FIELDS.entrySet().forEach(e -> {
					if (result.containsKey(e.getKey())) {
						Object valueObject = result.get(e.getKey());
						String value = (valueObject != null ? valueObject.toString() : "");
						PipelineProvidedMetadataEntry metadataEntry = new PipelineProvidedMetadataEntry(value, "text", analysis);
						metadataEntries.put(e.getValue() + " (v"+workflowVersion+")", metadataEntry);
					}
				});

				//convert the string/entry Map to a Set of MetadataEntry.  This has the same function as the old metadataTemplateService.getMetadataMap
				Set<MetadataEntry> metadataSet = metadataTemplateService.convertMetadataStringsToSet(metadataEntries);

				//save metadata back to sample
				samples.forEach(s -> {
					sampleService.mergeSampleMetadata(sample,metadataSet);
				});

			} else {
				throw new PostProcessingException("SISTR results for file are not correctly formatted");
			}

		} catch (IOException e) {
			throw new PostProcessingException("Error parsing JSON from SISTR results", e);
		} catch (IridaWorkflowNotFoundException e) {
			throw new PostProcessingException("Workflow is not found", e);
		}
	}

	/**
	 * Appends the name and version together for a metadata field name.
	 * 
	 * @param name    The name.
	 * @param version The version.
	 * @return The appended name and version.
	 */
	private String appendVersion(String name, String version) {
		return name + "(" + version + ")";
	}

	
	@Override
	public AnalysisType getAnalysisType() {
		return SISTRplugin.SISTR_TYPING_PLUGIN;
	}
}