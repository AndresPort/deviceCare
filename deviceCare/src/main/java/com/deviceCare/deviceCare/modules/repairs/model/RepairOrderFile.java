package com.deviceCare.deviceCare.modules.repairs.model;

import com.deviceCare.deviceCare.config.PostgreSQLEnumType;
import com.deviceCare.deviceCare.config.RepairOrderFileStageType;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairOrderFileStage;
import com.deviceCare.deviceCare.modules.users.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "repair_order_files")
public class RepairOrderFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_order_id", nullable = false)
    private RepairOrder repairOrder;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Type(RepairOrderFileStageType.class)
    @Column(name = "stage", columnDefinition = "repair_order_file_stage")
    private RepairOrderFileStage stage;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;
}