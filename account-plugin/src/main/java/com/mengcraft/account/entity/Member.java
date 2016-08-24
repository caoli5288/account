package com.mengcraft.account.entity;

import com.mengcraft.account.util.SecureUtil;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "pre_ucenter_members")
public class Member {

    @Id
    private int uid;

    @Column(length = 15, unique = true)
    private String username;

    @Column(length = 32)
    private String password;

    @Column(length = 32, nullable = false)
    private String email;

    @Column(length = 15)
    private String regip;

    @Column
    private int regdate;

    @Column(length = 6)
    private String salt;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "member")
    private AppAccountBinding binding;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Set new password and a random salt.
     *
     * @param rawPassword The password will be encrypted automatic.
     */
    public void setRawPassword(String rawPassword) {
        SecureUtil util = SecureUtil.DEFAULT;
        setSalt(util.random(3));
        try {
            setPassword(util.digest(util.digest(rawPassword) + getSalt()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean valid(String in) {
        SecureUtil util = SecureUtil.DEFAULT;
        try {
            in = util.digest(util.digest(in) + getSalt());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getPassword().equals(in);
    }

    /**
     * @return {@code true} if is a registered user.
     */
    public boolean valid() {
        return getUid() != 0;
    }

    public String getRegip() {
        return regip;
    }

    public void setRegip(String regip) {
        this.regip = regip;
    }

    public int getRegdate() {
        return regdate;
    }

    public void setRegdate(int regdate) {
        this.regdate = regdate;
    }

    public AppAccountBinding getBinding() {
        return binding;
    }

    public void setBinding(AppAccountBinding binding) {
        this.binding = binding;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
