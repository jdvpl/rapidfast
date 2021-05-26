import React from "react";
import styled from "@emotion/styled";
import Layout from "../components/layouts/Layout"; //se llama el layout para reutilizarlo

const H1 = styled.h1`
  color: yellowgreen;
`;
const Home = () => {
  return (
    <Layout>
      {" "}
      {/* //!se llamar el layout el principal para reutilizarlas en todas las paginas*/}
      <H1>Kamisama</H1>
    </Layout>
  );
};

export default Home;
