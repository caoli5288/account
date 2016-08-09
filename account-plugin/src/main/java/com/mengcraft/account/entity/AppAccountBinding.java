package com.mengcraft.account.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * Created on 16-4-13.
 */
@Entity
public class AppAccountBinding {

    @Id
    private int uid;

    @Column(unique = true)
    private String binding;

    @JoinColumn(name = "uid")
    @OneToOne
    private Member member;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

}
