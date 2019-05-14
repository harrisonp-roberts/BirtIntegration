package integration;

import java.io.File;
import java.util.Scanner;

import org.eclipse.birt.report.engine.api.EngineException;

public class AppDriver {
	public static void main(String[] args) {
		Reporting reporting = Reporting.getInstance();
		File reportDesign = new File("C:/Workspace/reporting/testreport.rptdesign");
		Scanner input = new Scanner(System.in);
		String fileName;
		
		System.out.println("Enter filepath of report design, no input for default file: ");
		fileName = input.nextLine();
		input.close();
		
		if(fileName != null && !fileName.equals("") ) {
			reportDesign = new File(fileName);
		}
		
		try {
			reporting.execute(reportDesign, "pdf");
		} catch (EngineException e) {
			e.printStackTrace();
		}
		
		System.out.println("Finished!");
	}
	
}
