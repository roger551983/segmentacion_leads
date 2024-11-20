package com.example.workflow;


import camundajar.impl.com.google.gson.Gson;
import jakarta.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.Collection;


import static org.camunda.spin.Spin.JSON;


@Named
public class MyFirstService implements JavaDelegate {

  public static int contador =0;

  ArrayList<Object> lista_elementos= new ArrayList<>();

  Collection<?>     coleccion_clientes = new ArrayList<>(); {
  }




  @Override
  public void execute(DelegateExecution delegateExecution) {
    Logger logger = LoggerFactory.getLogger(MyFirstService.class);
    List<?> lista_coleccion= (List<?>)delegateExecution.getVariable("resultadoColeccion");
    List<?> lista_clientes = (List<?>)delegateExecution.getVariable("cliente");
    Object objeto_coleccion=lista_coleccion.get(0);
    logger.info("lista_elementos size = " + lista_elementos.size());
    lista_elementos.add(((HashMap<?, ?>) objeto_coleccion).get("clasificacion"));
    HashMap<String,Object> map_coleccion = new HashMap<>();
    map_coleccion.put("values",lista_elementos);
    Gson obj_gson = new Gson();
    String json_coleccion = obj_gson.toJson(map_coleccion);

    // fix by raghu - make it as JSON
    delegateExecution.setVariable("resultadoColeccion", JSON( json_coleccion ) );
    delegateExecution.setVariable("cliente",lista_clientes);
    logger.info(json_coleccion);
    logger.info(lista_clientes.toString());

    

  }


  }

