package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AssignOrderUseCaseTest {

    private IOrderPersistencePort persistence;
    private AssignOrderUseCase useCase;

    @BeforeEach
    void setup() {
        persistence = Mockito.mock(IOrderPersistencePort.class);
        useCase = new AssignOrderUseCase(persistence);
    }

    @Test
    @DisplayName("Should assign PENDIENTE order and set EN_PREPARACION")
    void ok() {
        Order o = new Order();
        o.setId(1L);
        o.setStatus(OrderStatus.PENDIENTE);
        when(persistence.findById(1L)).thenReturn(Optional.of(o));
        when(persistence.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order r = useCase.assignOrder(1L, 99L);
        assertThat(r.getEmployeeId()).isEqualTo(99L);
        assertThat(r.getStatus()).isEqualTo(OrderStatus.EN_PREPARACION);
    }

    @Test
    @DisplayName("Should fail when order not found or status invalid")
    void errors() {
        when(persistence.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.assignOrder(1L, 10L)).isInstanceOf(DomainException.class);

        Order o = new Order();
        o.setId(2L);
        o.setStatus(OrderStatus.LISTO);
        when(persistence.findById(2L)).thenReturn(Optional.of(o));
        assertThatThrownBy(() -> useCase.assignOrder(2L, 10L)).isInstanceOf(DomainException.class);
    }
}
