package cf.poorcoder.driverapplication.Models;

public class Notification {

    String target,category,content,date;

    public Notification(String target, String category, String content, String date) {
        this.target = target;
        this.category = category;
        this.content = content;
        this.date = date;
    }

    public Notification()
    {

    }
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
