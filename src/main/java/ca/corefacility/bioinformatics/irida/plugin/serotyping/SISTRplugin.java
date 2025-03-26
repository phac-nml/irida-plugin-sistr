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

	public static AnalysisType SISTR_TYPING_PLUGIN = new AnalysisType("SISTR_PLUGIN");

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
		public Optional<String> getAnalysisViewer() {
			return Optional.of("sistr");
		}

		@Override
		public UUID getDefaultWorkflowUUID() {
			return UUID.fromString("6e5a983d-0259-4e10-b721-88aaf1867ec9");
		}

		@Override
		public Optional<Color> getBackgroundColor() {
			return Optional.of(Color.decode("#ff6666"));
		}


	}
}
