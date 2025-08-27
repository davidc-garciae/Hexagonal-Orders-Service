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

class CancelOrderUseCaseTest {

    private IOrderPersistencePort persistence;
    private CancelOrderUseCase useCase;

    @BeforeEach
    void setup() {
        persistence = Mockito.mock(IOrderPersistencePort.class);
        useCase = new CancelOrderUseCase(persistence);
    }

    @Test
    @DisplayName("Should cancel when status PENDIENTE and owner matches")
    void ok() {
        Order o = new Order();
        o.setId(1L);
        o.setCustomerId(7L);
        o.setStatus(OrderStatus.PENDIENTE);
        when(persistence.findById(1L)).thenReturn(Optional.of(o));
        when(persistence.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order r = useCase.cancel(1L, 7L);
        assertThat(r.getStatus()).isEqualTo(OrderStatus.CANCELADO);
    }

    @Test
    @DisplayName("Should fail when not owner or status invalid")
    void errors() {
        Order o = new Order();
        o.setId(1L);
        o.setCustomerId(7L);
        o.setStatus(OrderStatus.EN_PREPARACION);
        when(persistence.findById(1L)).thenReturn(Optional.of(o));

        assertThatThrownBy(() -> useCase.cancel(1L, 8L)).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> useCase.cancel(1L, 7L)).isInstanceOf(DomainException.class);
    }
}
