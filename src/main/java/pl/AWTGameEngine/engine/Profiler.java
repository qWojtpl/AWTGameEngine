package pl.AWTGameEngine.engine;

import java.util.LinkedHashMap;

public class Profiler {

    private final String profilerName;
    private final LinkedHashMap<String, Long> steps = new LinkedHashMap<>();
    private final ShowMethod showMethod;

    public Profiler(String name) {
        this.profilerName = name;
        this.showMethod = ShowMethod.SYSTEM;
        step("start");
    }

    public Profiler(String name, ShowMethod showMethod) {
        this.profilerName = name;
        this.showMethod = showMethod;
        step("start");
    }

    public void step() {
        step("next_step");
    }

    public void step(String name) {
        steps.put(name, System.nanoTime());
    }

    public void end() {
        step("end");
    }

    public void show() {
        StringBuilder builder = new StringBuilder();
        builder.append(profilerName).append(" profiler steps: ");
        long time = 0;
        long totalMs = 0;
        for(String stepName : steps.keySet()) {
            if(time != 0) {
                float ms = (steps.get(stepName) - time) / 1_000_000f;
                builder.append(" -> ").append(ms).append("ms -> ");
                totalMs += ms;
            }
            builder.append(stepName);
            time = steps.get(stepName);
        }
        builder.append(" (total ").append(totalMs).append("ms)");
        if(ShowMethod.SYSTEM.equals(showMethod)) {
            System.out.println("[PROFILER] " + builder);
        } else if(ShowMethod.LOGGER.equals(showMethod)) {
            Logger.info(builder.toString());
        }
    }

    public String getProfilerName() {
        return this.profilerName;
    }

    public LinkedHashMap<String, Long> getSteps() {
        return this.steps;
    }

    public ShowMethod getShowMethod() {
        return this.showMethod;
    }

    public enum ShowMethod {

        LOGGER,
        SYSTEM

    }

}
