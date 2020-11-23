package me.chawon.kakaopay.spread.domain;

import lombok.*;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@ToString(exclude = {"spreadEntity", "version"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Table(name = "pickup")
public class PickupEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pickup_id")
    private Long id;

    @NonNull
    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "pick_id")
    private Long pickId;

    @Column(name = "pick_at")
    private LocalDateTime pickAt;

    @Version
    private int version;

    @NonNull
    @ManyToOne(targetEntity = SpreadEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "spread_id")
    private SpreadEntity spreadEntity;


    public boolean isPicked(){
        return Objects.nonNull(pickId);
    }

    public void updatePickup(Long pickId){
        this.pickId = pickId;
        this.pickAt = LocalDateTime.now();
    }

    @Builder
    public PickupEntity(Long amount, SpreadEntity spreadEntity){
        this.amount = amount;
        this.spreadEntity = spreadEntity;
    }
}
