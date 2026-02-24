import java.util.*;

public class ProjectScheduler {

    private static final int MAX_WORKING_DAYS = 5;
    private static final int WEEK_DEADLINE_LIMIT = 7;

    private List<project> missedProjects = new ArrayList<>();
    private List<project> futureProjects = new ArrayList<>();

    private Scanner scanner = new Scanner(System.in);

    public void generateThisWeekSchedule(ProjectDoa dao) throws Exception {

        List<project> projects = dao.getAllProjects();

        if (projects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        System.out.print("Enter Scheduling Day (Sat/Sun): ");
        String schedulingDay = scanner.next().toLowerCase();

        System.out.print("Enter Busy Days (0-5): ");
        int busyTillDay = scanner.nextInt();

        System.out.print("Enter Last Week Avg Profit: ");
        double lastWeekAvg = scanner.nextDouble();

        System.out.print("Enter Last 3 Week Avg Profit: ");
        double lastThreeWeekAvg = scanner.nextDouble();

        List<project> schedule =
                generateOptimalSchedule(
                        projects,
                        schedulingDay,
                        busyTillDay);

        printSchedule(schedule, busyTillDay, "THIS WEEK");
    }

    public void generateNextWeekSchedule() {

        if (futureProjects.isEmpty()) {

            System.out.println("\n===== Schedule for NEXT WEEK =====");
            System.out.println("No projects available.");

            return;
        }

        System.out.print("Enter Last Week Avg Profit: ");
        double lastWeekAvg = scanner.nextDouble();

        System.out.print("Enter Last 3 Week Avg Profit: ");
        double lastThreeWeekAvg = scanner.nextDouble();

        List<project> sortedFuture =
                new ArrayList<>(futureProjects);

        sortedFuture.sort((a, b) ->
                Double.compare(
                        b.getExpectedRevenue(),
                        a.getExpectedRevenue()));

        List<project> schedule =
                new ArrayList<>();

        for (int i = 0;
             i < Math.min(
                     MAX_WORKING_DAYS,
                     sortedFuture.size());
             i++) {

            schedule.add(sortedFuture.get(i));
        }

        printSchedule(schedule, 0, "NEXT WEEK");
    }

    public List<project> generateOptimalSchedule(
            List<project> projects,
            String schedulingDay,
            int busyTillDay) {

        missedProjects.clear();
        futureProjects.clear();

        project[] schedule =
                new project[MAX_WORKING_DAYS];

        List<project> critical =
                new ArrayList<>();

        int planningOffset =
                schedulingDay.equals("sunday") ? 1 : 2;

        for (project p : projects) {

            int daysPassed =
                    getDaysBetween(
                            p.getSubmissionDay(),
                            schedulingDay);

            int remaining =
                    p.getDeadline()
                            - (daysPassed + planningOffset);

            p.setRemainingDeadline(remaining);

            if (remaining < 0) {

                p.setMissReason(
                        "Expired (Deadline Passed)");

                missedProjects.add(p);
            }
            else if (remaining <= WEEK_DEADLINE_LIMIT) {

                critical.add(p);
            }
            else {

                futureProjects.add(p);
            }
        }

        critical.sort((a, b) ->
                Double.compare(
                        b.getExpectedRevenue(),
                        a.getExpectedRevenue()));

        int pointer = busyTillDay;

        for (project p : critical) {

            if (pointer < MAX_WORKING_DAYS) {

                schedule[pointer] = p;

                pointer++;
            }
            else {

                p.setMissReason(
                        "Missed (Low Revenue Priority)");

                missedProjects.add(p);
            }
        }

        Iterator<project> it =
                futureProjects.iterator();

        while (it.hasNext()) {

            project p = it.next();

            int nextWeekRemaining =
                    p.getRemainingDeadline()
                            - MAX_WORKING_DAYS;

            if (nextWeekRemaining < 0) {

                p.setMissReason(
                        "Expired (Deadline Passed)");

                missedProjects.add(p);

                it.remove();
            }
            else {

                p.setRemainingDeadline(
                        nextWeekRemaining);
            }
        }

        List<project> result =
                new ArrayList<>();

        for (int i = busyTillDay;
             i < MAX_WORKING_DAYS;
             i++) {

            if (schedule[i] != null)
                result.add(schedule[i]);
        }

        return result;
    }

    private void printSchedule(
            List<project> schedule,
            int startDay,
            String week) {

        System.out.println(
                "\n===== Schedule for "
                        + week
                        + " =====");

        int day = startDay + 1;

        double totalProfit = 0;

        for (project p : schedule) {

            if (week.equals("THIS WEEK")) {

                System.out.println(
                        "Day "
                                + day++
                                + " → "
                                + p.getTitle()
                                + " | Revenue: ₹"
                                + p.getExpectedRevenue());
            }
            else {

                System.out.println(
                        "Day "
                                + day++
                                + " → "
                                + p.getTitle()
                                + " | Revenue: ₹"
                                + p.getExpectedRevenue()
                                + " | Remaining Deadline: "
                                + p.getRemainingDeadline()
                                + " days");
            }

            totalProfit +=
                    p.getExpectedRevenue();
        }

        System.out.println(
                "\n💰 Total Profit: ₹"
                        + totalProfit);

        if (week.equals("THIS WEEK")) {

            System.out.println(
                    "\n===== Missed Projects =====");

            if (missedProjects.isEmpty()) {

                System.out.println("None");
            }
            else {

                for (project p :
                        missedProjects) {

                    if ("Missed (Low Revenue Priority)"
                            .equals(
                                    p.getMissReason())) {

                        System.out.println(
                                p.getTitle()
                                        + " | Reason: "
                                        + p.getMissReason());
                    }
                    else {

                        System.out.println(
                                p.getTitle()
                                        + " | Reason: "
                                        + p.getMissReason()
                                        + " | Remaining Deadline: "
                                        + p.getRemainingDeadline()
                                        + " days");
                    }
                }
            }

            System.out.println(
                    "\n===== Future Projects =====");

            if (futureProjects.isEmpty()) {

                System.out.println("None");
            }
            else {

                for (project p :
                        futureProjects) {

                    System.out.println(
                            p.getTitle()
                                    + " | Remaining Deadline: "
                                    + p.getRemainingDeadline()
                                    + " days");
                }
            }
        }
    }

    public static int getDaysBetween(
            String sub,
            String sch) {

        List<String> days =
                Arrays.asList(
                        "monday",
                        "tuesday",
                        "wednesday",
                        "thursday",
                        "friday",
                        "saturday",
                        "sunday");

        int diff =
                days.indexOf(sch)
                        - days.indexOf(sub);

        if (diff < 0)
            diff += 7;

        return diff;
    }
}