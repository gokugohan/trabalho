package com.projetoaplicado.heldermenezes.trabalho.interfaces;



import com.projetoaplicado.heldermenezes.trabalho.model.AcertoDeConta;

import java.util.List;

/**
 * Created by heldermenezes on 23/06/2015.
 */
public interface IConta {
    void onInitEnvioDedados(List<AcertoDeConta> acertoDeContas);
}
