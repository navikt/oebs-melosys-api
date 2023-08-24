package no.nav.oebs.melosys.db.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
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
@ToString
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

        @Column(name = "FODSELSNR")
        private BigDecimal fodselsnr;

        @Column(name = "FULLMEKTIG_ORG")
        private String fullmektigOrgnr;

        @Column(name = "FULLMEKTIG_FODSELSNR")
        private BigDecimal fullmektigFnr;

        @Column(name = "FAKTURA_DATO")
        private LocalDateTime fakturaDato;

        @NotBlank
        @Column(name = "VEDTAKSID")
        private String vedtaksId;

        @NotBlank
        @Column(name = "FAKTURAREFNR")
        private String fakturaRefNr;

        @NotBlank
        @Column(name = "KREDITTREFNR")
        private String kredittRefNr;

        @NotBlank
        @Column(name = "REFERANSEBRUKER")
        private String referanseBruker;

        @NotBlank
        @Column(name = "REFERANSENAV")
        private String refranseNAV;

        @NotBlank
        @Column(name = "FAKTURABESKRIVELSE")
        private String fakturaBeskrivelse;

       // @OneToMany(mappedBy = "XXRTV_MEL_FAKTURA") // cascade osv?
       // private List<FakturaLinje> fakturalinjer;

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

