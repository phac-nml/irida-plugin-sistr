package ca.corefacility.bioinformatics.irida.plugin.serotyping;

import java.awt.Color;
import java.util.Optional;
import java.util.UUID;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginException;
import org.pf4j.PluginWrapper;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.AnalysisSampleUpdater;
import ca.corefacility.bioinformatics.irida.plugins.IridaPlugin;
import ca.corefacility.bioinformatics.irida.plugins.IridaPluginException;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;


public class SISTRplugin extends Plugin {

	public static AnalysisType SISTR_TYPING_PLUGIN = new AnalysisType("SISTR_TYPING_PLUGIN");

	public SISTRplugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
	public void start() throws PluginException {
	}

	@Extension
	public static class PluginInfo implements IridaPlugin {

		@Override
		public Optional<AnalysisSampleUpdater> getUpdater(MetadataTemplateService metadataTemplateService,
				SampleService sampleService, IridaWorkflowsService iridaWorkflowsService) throws IridaPluginException {
			/**return Optional.empty();**/
			return Optional.of(new SISTRSampleUpdater(metadataTemplateService, sampleService, iridaWorkflowsService));
		}

		@Override
		public AnalysisType getAnalysisType() {
			return SISTR_TYPING_PLUGIN;
		}

		@Override
		public UUID getDefaultWorkflowUUID() {
			return UUID.fromString("b21ea62c-7916-4ca6-96ba-90c20177b70f");
		}

		@Override
		public Optional<Color> getBackgroundColor() {
			return Optional.of(Color.decode("#ff6666"));
		}
	}
}
