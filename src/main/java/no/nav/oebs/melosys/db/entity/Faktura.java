package no.nav.oebs.melosys.db.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Entitetsklasse som representerer en rad i faktura-tabellen.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Table(name = "XXRTV_MEL_FAKTURA")
public class Faktura {

    @Id
    private Long id;

        @GenericGenerator(
                name = "xxrtv_mel_fak_seq",
                strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
                parameters = {
                        @Parameter(name = "sequence_name", value = "XXRTV_MEL_FAK_SEQ"),
                        @Parameter(name = "initial_value", value = "1"),
                        @Parameter(name = "increment_size", value = "1")
                }
        )
        @Id
        @GeneratedValue(generator = "xxrtv_mel_fak_seq")

        @Column(name = "FAKTURA_ID")
        private Long fakturaId;

        @NotBlank
        @Size(min = 11, max = 11)
        @Column(name = "FODSELSNR", length = 11)
        private String fodselsnr;

        @Column(name = "FAKTURA_DATO")
        private LocalDateTime fakturaDato;

        @Column(name = "VEDTAKSID")
        private String vedtaksId;

        @NotBlank
        @Column(name = "FAKTURAREFNR")
        private LocalDateTime fakturaRefNr;

        @Column(name = "KREDITTREFNR")
        private String kredittRefNr;

        @Column(name = "REFERANSEBRUKER")
        private String referanseBruker;

        @NotBlank
        @Column(name = "REFERANSENAV")
        private String refranseNAV;

        @NotNull
        @Column(name = "FAKTURABESKRIVELSE")
        private String fakturaBeskrivelse;

        //@NotBlank
        //@OneToMany(mappedBy = "fakturaId") // cascade osv?
        //Map<String, Object> fakturalinjer;
        //private List<FakturaLinje> fakturalinjer;

        @Column(name = "FAKTURAJSON")
        private String fakturaJson;

        @Column(name = "KORRELASJON_ID")
        private String korrelasjonId;

        @Column(name = "STATUS")
        private String status;

        @Column(name = "RETRY_TELLER")
        private Integer retryTeller;

        @Column(name = "RETRY_TIDSPUNKT")
        private LocalDateTime retryTidspunkt;

        @Column(name = "FEILINFORMASJON")
        private String feilinformasjon;
}

