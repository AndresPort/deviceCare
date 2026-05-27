package com.deviceCare.deviceCare.modules.clients.model;

import com.deviceCare.deviceCare.common.base.BaseEntity;
import com.deviceCare.deviceCare.modules.users.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "document_number", unique = true, length = 50)
    private String documentNumber;

    @Column(name = "address")
    private String address;
}