package com.toeic.online.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A HistoryContactDetails.
 */
@Entity
@Table(name = "history_contact_details")
public class HistoryContactDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "history_contact")
    private String historyContact;

    @Column(name = "is_open")
    private Boolean isOpen;

    @Column(name = "status")
    private Boolean status;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HistoryContactDetails id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHistoryContact() {
        return this.historyContact;
    }

    public HistoryContactDetails historyContact(String historyContact) {
        this.setHistoryContact(historyContact);
        return this;
    }

    public void setHistoryContact(String historyContact) {
        this.historyContact = historyContact;
    }

    public Boolean getIsOpen() {
        return this.isOpen;
    }

    public HistoryContactDetails isOpen(Boolean isOpen) {
        this.setIsOpen(isOpen);
        return this;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public HistoryContactDetails status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistoryContactDetails)) {
            return false;
        }
        return id != null && id.equals(((HistoryContactDetails) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistoryContactDetails{" +
            "id=" + getId() +
            ", historyContact='" + getHistoryContact() + "'" +
            ", isOpen='" + getIsOpen() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
