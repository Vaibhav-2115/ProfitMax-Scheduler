public class project {

    private int projectId;
    private String title;
    private int deadline;
    private double expectedRevenue;

    public project(String title, int deadline, double expectedRevenue) {
        this.title = title;
        this.deadline = deadline;
        this.expectedRevenue = expectedRevenue;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public int getDeadline() {
        return deadline;
    }

    public double getExpectedRevenue() {
        return expectedRevenue;
    }

    @Override
    public String toString() {
        return "Project{" +
                "ID=" + projectId +
                ", Title='" + title + '\'' +
                ", Deadline=" + deadline +
                ", Revenue=" + expectedRevenue +
                '}';
    }
}
