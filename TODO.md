# TODO - Port a Minecraft 1.21.1 (NeoForge)

Estado actual: el proyecto compila (`compileJava`) y carga en entorno de desarrollo tras corregir el descriptor (`neoforge.mods.toml`) y las rutas de resources. Quedan tareas para completar el port funcional/visual.

## GeckoLib 4 (animaciones y modelo)
- [ ] Implementar controladores en `EntityAirship#registerControllers(AnimatableManager.ControllerRegistrar)` usando las animaciones de `assets/classicexploration/animations/entity/airship.animation.json`.
  - [ ] Controladores/estados para inclinación del motor (arriba/abajo/neutro).
  - [ ] Controladores/estados para actividad de motores (izquierdo/derecho) según inputs/datos sincronizados (`DATA_ID_ENGINE_LEFT_ACTIVE`, `DATA_ID_ENGINE_RIGHT_ACTIVE`, etc.).
  - [ ] Ajustar nombres de animaciones/triggers para que coincidan exactamente con el archivo `.animation.json`.
- [ ] Verificar que el modelo `assets/classicexploration/geo/entity/airship.geo.json` sea compatible con GeckoLib 4 y que los nombres de huesos/bones referenciados por las animaciones existan.
- [ ] Revisar `RenderAirship` (textura, escala, rotaciones, sombras) y añadir capas/render states si hacen falta (por ejemplo color del globo, estado de daño, equipamiento).

## Entidad `EntityAirship` (comportamiento y lógica)
- [x] Implementar la lógica de **combustible** de forma funcional (consumo desde slots 0-1, `getBurnTime(RecipeType.SMELTING)`, `remainingFuelBurnTime`/`DATA_ID_HAS_FUEL`, persistencia en NBT, manejo básico de restos de crafteo).
  - [ ] Separar estado por motor si el diseño original lo requería (hay TODO en el código original sobre motor izquierdo/derecho).
- [x] Ajustar y validar el movimiento/controles en 1.21.1 (aceleración/frenado, giro, ascenso/descenso, deriva/caída suave sin piloto o combustible, control por pasajero).
  - [ ] Sincronización cliente-servidor de inputs y movimiento (evitar jitter/desync).
  - [ ] Condiciones de colisión/empuje con barcos, jugadores y otras entidades.
- [ ] Revisar `interact`/montaje:
  - [ ] Posiciones de asiento(s) para piloto/pasajeros (`positionRider`/control de pasajero controlador) y prioridad del pasajero que controla.
  - [x] Interacción para abrir inventario (Shift+clic derecho y tecla `E` montado) con GUI adaptada.
  - [ ] Uso de cofres por tag `classicexploration:chests` y comportamiento al instalar/quitar cofre (drop de contenido, cambio de tamaño de inventario).

## Ítem de la aeronave (`airship_item`)
- [x] Implementar la funcionalidad del ítem `classicexploration:airship_item` (hoy solo existe para la receta):
  - [x] Colocar/spawnear `EntityAirship` al usar el ítem en el mundo (con validaciones de espacio/colisión, consumo en survival, orientación inicial).
  - [ ] (Opcional) Guardar/restaurar estado relevante (color, combustible, contenido, equipamiento) si se quiere recoger la aeronave como ítem.

## Inventario, menú y capacidades (NeoForge 1.21.1)
- [x] Implementar GUI de inventario adaptada a `textures/gui/airship_inventory.png` y `textures/gui/airship_chest_inventory.png` (2 slots de combustible; 4x3=12 slots de cofre cuando está instalado) con `AirshipMenu`/`AirshipScreen` y apertura por `Shift+clic derecho` y tecla `E` al ir montado.
- [ ] Reimplementar acceso al inventario según APIs actuales:
  - [ ] Exponer item handler/capability de la entidad de forma compatible con NeoForge 1.21.1 (el port eliminó la integración antigua de `IItemHandler`/`LazyOptional`).
  - [ ] Sincronizar contenido del inventario al guardar/cargar (`readAdditionalSaveData`/`addAdditionalSaveData` con `ContainerHelper` y `registryAccess`) y al abrir menús.
- [ ] Definir tamaños/slots según equipamiento (`E_AIRSHIP_EQUIPMENT`: `NONE`, `CHEST`, `CANNON`, `LAMP`) de forma completa (actualmente cofre mapeado a 12 slots según GUI).

## Networking
- [ ] Revisar el payload `CPacketAirshipControls`:
  - [ ] Validaciones de seguridad en servidor (distancia, que el jugador sea controlador del vehículo, rate/consistencia de inputs).
  - [ ] Confirmar que el registro en `RegisterPayloadHandlersEvent` y el `versioned(...)` son los adecuados para el entorno objetivo.
- [ ] Añadir paquetes adicionales si se requieren para sincronizar estados que no quepan en `SynchedEntityData` (inventario, equipamiento, animaciones disparadas por eventos, etc.).

## Registro, atributos y contenido
- [ ] Revisar si la entidad necesita atributos/eventos de registro adicionales (por ejemplo `EntityAttributeCreationEvent`) según el tipo/base usada.
- [ ] Añadir ítems/entidad a pestañas creativas de 1.21.1 (`BuildCreativeModeTabContentsEvent` o tabs propias) para facilitar pruebas.
- [ ] Confirmar claves de idioma en `assets/classicexploration/lang/en_us.json`:
  - [ ] Nombre de la entidad (`entity.classicexploration.airship`) y del ítem `airship_item` si corresponde.
  - [ ] Tooltips y textos de UI/menús nuevos.

## Recursos y datos (assets/data)
- [ ] Validar in-game que todas las texturas/modelos cargan sin errores (textura de entidad, ítems, partículas si aplica).
- [ ] Revisar `sounds.json` y disponibilidad del sonido de cañón (`entity.airship.cannon.shoot` está registrado en código pero no definido en `sounds.json`; `propellor_loop` sí existe).
- [ ] Limpiar duplicados de datapack heredados del formato antiguo (mantener solo las rutas 1.21):
  - [ ] `data/classicexploration/recipes/` (antiguo) vs `data/classicexploration/recipe/` (nuevo).
  - [ ] `data/classicexploration/tags/items/` (antiguo) vs `data/classicexploration/tags/item/` (nuevo).
- [ ] Revisar/actualizar tags convencionales NeoForge (`c:*`) usados en recetas para que existan en 1.21.1 (`c:strings`, `c:ingots/iron`, `c:leathers`, etc.) o proporcionar equivalentes/compatibilidad.

## Build y calidad
- [ ] Ejecutar `.\gradlew.bat build` completo (no solo `compileJava`) y corregir avisos/errores restantes.
- [ ] Probar `runClient` y `runServer`: carga del mod, registro de contenido, render de la entidad, interacciones y ausencia de errores en `latest.log`.
- [ ] (Opcional) Eliminar o excluir del flujo de build artefactos heredados en `build/` y datos de `run/` que no deban versionarse.
- [ ] (Opcional) Revisar metadatos (`mods.toml`: `version`, créditos, logo, descripción) y empaquetado final del jar.