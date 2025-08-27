package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IOrderEventPublisherPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MarkOrderReadyUseCaseTest {

  private IOrderPersistencePort persistence;
  private IOrderEventPublisherPort publisher;
  private MarkOrderReadyUseCase useCase;

  @BeforeEach
  void setup() {
    persistence = Mockito.mock(IOrderPersistencePort.class);
    publisher = Mockito.mock(IOrderEventPublisherPort.class);
    useCase = new MarkOrderReadyUseCase(persistence, publisher);
  }

  @Test
  @DisplayName("Should mark as LISTO and generate PIN and publish event")
  void ok() {
    Order o = new Order();
    o.setId(1L);
    o.setStatus(OrderStatus.EN_PREPARACION);
    when(persistence.findById(1L)).thenReturn(Optional.of(o));
    when(persistence.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

    Order r = useCase.markReady(1L);
    assertThat(r.getStatus()).isEqualTo(OrderStatus.LISTO);
    assertThat(r.getPin()).isNotBlank();
    verify(publisher).publishOrderStatusChanged(any(Order.class), Mockito.eq("EN_PREPARACION"));
  }

  @Test
  @DisplayName("Should fail when status is not EN_PREPARACION")
  void invalidStatus() {
    Order o = new Order();
    o.setId(1L);
    o.setStatus(OrderStatus.PENDIENTE);
    when(persistence.findById(1L)).thenReturn(Optional.of(o));

    assertThatThrownBy(() -> useCase.markReady(1L)).isInstanceOf(DomainException.class);
  }
}
