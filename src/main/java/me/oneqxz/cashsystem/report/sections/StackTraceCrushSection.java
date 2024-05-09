package me.oneqxz.cashsystem.report.sections;

public class StackTraceCrushSection implements ICrashSection {


    @Override
    public String formatSection(Throwable crush) {
        StringBuilder sb = new StringBuilder();
        sb.append("-- Head --\n");
        sb.append("Thread: %s\n".formatted(Thread.currentThread().getName()));
        sb.append("Stacktrace: \n");
        for(StackTraceElement element : crush.getStackTrace())
            sb.append("\t at %s\n".formatted(element));

        return sb.toString();
    }
}
