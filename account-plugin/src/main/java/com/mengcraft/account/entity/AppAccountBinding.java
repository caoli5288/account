package com.mengcraft.account.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.UUID;

/**
 * Created on 16-4-13.
 */
@Entity
public class AppAccountBinding {

    @Id
    private int uid;

    @Column(unique = true)
    private String binding;

    @Column(unique = true, nullable = false)
    private UUID bindingId;

    private String name;

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

    public UUID getBindingId() {
        return bindingId;
    }

    public void setBindingId(UUID bindingId) {
        this.bindingId = bindingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

}
