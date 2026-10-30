package com.ex.eventpro_api.payment.internal.mapper;

import com.ex.eventpro_api.payment.internal.dto.response.PaymentResponse;
import com.ex.eventpro_api.payment.internal.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    // On n'a plus besoin de @Mapping(target = "reservationId", source = "reservation.id")
    PaymentResponse toResponse(Payment payment);
}