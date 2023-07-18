package txtparsing;

public class MyDocProject {


    private String caption;

    public MyDocProject( String caption) {
        
        this.caption = caption;
        }

    @Override
    public String toString() {
        String ret = "MyDoc{"
                + "\n\tCaption: " + caption;
        return ret + "\n}";
    }

    //---- Getters & Setters definition ----
   
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

}
