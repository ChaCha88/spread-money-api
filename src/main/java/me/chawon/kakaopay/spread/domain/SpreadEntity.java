package me.chawon.kakaopay.spread.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "spread")
public class SpreadEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spread_id")
    private Long id;

    @Column(name = "token", length = 3, nullable = false, unique = true)
    private String token;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "person", nullable = false)
    private int person;

    @Setter
    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "spread_id")
    private List<PickupEntity> pickups= new ArrayList<>();

    public void addPickups(Long amount){
        this.pickups.add(PickupEntity.builder()
                .amount(amount)
                .spreadEntity(this)
                .build()
        );
    }

    public boolean isPickUpPossible(long expiredTime){
        return LocalDateTime.now().minusMinutes(expiredTime).isAfter(this.createAt);
    }

    public boolean isShowInfoPossible(long expiredDay){
        return LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).minusDays(expiredDay).isAfter(this.createAt);
    }

    public boolean isDifferentRoom(String roomId){
        return !roomId.equals(this.roomId);
    }

    @Builder
    public SpreadEntity(String token, String roomId, Long userId, Long amount, int person){
        this.token = token;
        this.roomId = roomId;
        this.userId = userId;
        this.amount = amount;
        this.person =  person;
    }
 
}
