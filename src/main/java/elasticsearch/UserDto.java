package elasticsearch;


public class UserDto {
    private String first_name;
    private String last_name;
    private int age;
    private String about;
    private String[] interests;

    public String getFirst_name() {
        return first_name;
    }

    public UserDto setFirst_name(String first_name) {
        this.first_name = first_name;
        return this;
    }

    public String getLast_name() {
        return last_name;
    }

    public UserDto setLast_name(String last_name) {
        this.last_name = last_name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public UserDto setAge(int age) {
        this.age = age;
        return this;
    }

    public String getAbout() {
        return about;
    }

    public UserDto setAbout(String about) {
        this.about = about;
        return this;
    }

    public String[] getInterests() {
        return interests;
    }

    public UserDto setInterests(String[] interests) {
        this.interests = interests;
        return this;
    }
}
