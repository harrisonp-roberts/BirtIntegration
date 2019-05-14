package integration;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLActionHandler;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.birt.report.engine.api.IRunTask;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

public class Reporting {
	private static final Reporting INSTANCE = new Reporting();
	private final String TEMP_FILE = "C:/temp/report.rptdocument";

	private IReportEngine engine;
	private EngineConfig config;
	private String exportType = "html";

	public static Reporting getInstance() {
		return INSTANCE;
	}

	private Reporting() {
		try {
			config = new EngineConfig();
			config.setLogConfig(null, Level.FINE);
			Platform.startup();

			final IReportEngineFactory FACTORY = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);

			engine = FACTORY.createReportEngine(config);
			engine.changeLogLevel(Level.WARNING);
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	void execute(File reportDesignFile, String exportType) throws EngineException {
		this.exportType = exportType;
		final RenderOption renderDesignOption = new RenderOption();
		final HTMLServerImageHandler imgHandler = new HTMLServerImageHandler();

		renderDesignOption.setActionHandler(new HTMLActionHandler());
		renderDesignOption.setImageHandler(imgHandler);

		IReportDocument reportDocument = run(renderDesignOption, reportDesignFile);
		render(renderDesignOption, reportDocument);

		Platform.shutdown();
	}

	/**
	 * Runs the report design template through the engine to create a report
	 * document that can then be exported as the chosen output file
	 * 
	 * @param renderDesignOption
	 * @param reportDesignFile
	 * @return
	 * @throws EngineException
	 */
	IReportDocument run(RenderOption renderDesignOption, File reportDesignFile) throws EngineException {
		IReportRunnable reportDesign = null;

		config.getEmitterConfigs().put(exportType, renderDesignOption);
		reportDesign = engine.openReportDesign(reportDesignFile.getAbsolutePath());
		
		final IRunTask runTask = engine.createRunTask(reportDesign);
		runTask.run(TEMP_FILE);

		final IReportDocument reportDocument = engine.openReportDocument(TEMP_FILE);

		runTask.close();
		return reportDocument;
	}

	/**
	 * Runs the report document through the engine and exports as chosen file type
	 * 
	 * @param renderDesignOption
	 * @param reportDocument
	 * @throws EngineException
	 */
	void render(RenderOption renderDesignOption, IReportDocument reportDocument) throws EngineException {
		final IRenderTask renderTask = engine.createRenderTask(reportDocument);
		final HashMap<String, RenderOption> contextMap = new HashMap<String, RenderOption>();
		final RenderOption finalRenderOption = new RenderOption();

		contextMap.put("APPCONTEXT_" + exportType.toUpperCase() + "_RENDER_CONTEXT", renderDesignOption);
		renderTask.setAppContext(contextMap);

		finalRenderOption.setOutputFileName("C:/Workspace/reporting/testreport." + exportType);
		finalRenderOption.setOutputFormat(exportType);
		renderTask.setRenderOption(finalRenderOption);
		renderTask.render();
		renderTask.close();
	}
}
