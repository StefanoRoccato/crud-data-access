package it.svg.crud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "crud.db")
public class CrudDbProperties {
    private String user;
    private String password;
    private String tns;
    private String connName;
    private String tnsAdmin;

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getTns() { return tns; }
    public void setTns(String tns) { this.tns = tns; }
    public String getConnName() { return connName; }
    public void setConnName(String connName) { this.connName = connName; }
    public String getTnsAdmin() { return tnsAdmin; }
    public void setTnsAdmin(String tnsAdmin) { this.tnsAdmin = tnsAdmin; }
}
