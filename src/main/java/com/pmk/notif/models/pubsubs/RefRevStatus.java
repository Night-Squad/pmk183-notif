package com.pmk.notif.models.pubsubs;


import javax.persistence.*;

@Entity
@Table(name = "ref_rev_status", schema = "public")
public class RefRevStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_ref_rev_status_id_seq")
    @SequenceGenerator(name = "generator_ref_rev_status_id_seq", sequenceName = "ref_rev_status_id_seq", schema = "public", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "status_name")
    private String statusName;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    public RefRevStatus() {
    }

    public RefRevStatus(Long id) {
        this.id = id;
    }

    public RefRevStatus(Long id, String statusName, String description, Boolean isActive) {
        this.id = id;
        this.statusName = statusName;
        this.description = description;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "RefRevStatus{" +
                "id=" + id +
                ", statusName='" + statusName + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
