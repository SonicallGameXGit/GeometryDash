package me.oneqxz.cashsystem;

import lombok.extern.log4j.Log4j2;
import me.oneqxz.cashsystem.report.CrashReport;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Log4j2
public final class CrashSystem {

    public static void printCrashReport(CrashReport report)
    {
        File dir = new File("crash-reports");
        if(!dir.exists())
            dir.mkdirs();

        File crashFile = new File(dir, String.format("crash-%s.txt", new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date())));
        try
        {
            String generatedReport = report.toString();
            System.out.println(generatedReport);

            FileUtils.writeByteArrayToFile(crashFile, generatedReport.getBytes(StandardCharsets.UTF_8));
            log.fatal("#%!%# Game crashed! Crash report saved to: {}", crashFile.getAbsoluteFile());
        }
        catch (IOException e)
        {
            log.fatal("#%!%# Game crashed! Crash report could not be saved. #%?%#");
        }
    }

}
