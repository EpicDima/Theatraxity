package com.epicdima.theatraxity.services;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.usecases.user.GetRawUserUseCase;

/**
 * @author EpicDima
 */
@Singleton
public final class VerifyUserService {
    private final GetRawUserUseCase getRawUserUseCase;

    @Inject
    public VerifyUserService(GetRawUserUseCase getRawUserUseCase) {
        this.getRawUserUseCase = getRawUserUseCase;
    }

    public GetRawUserUseCase getRawUserUseCase() {
        return getRawUserUseCase;
    }
}
