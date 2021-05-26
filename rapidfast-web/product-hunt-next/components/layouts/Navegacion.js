import React from "react";
import Link from "next/link";
const Navegacion = () => {
  return (
    <nav>
      <Link href="/">Inicio</Link>
      <Link href="/politica">Politicas</Link>
      <Link href="/nuevo">Nuevo Producto</Link>
    </nav>
  );
};

export default Navegacion;
