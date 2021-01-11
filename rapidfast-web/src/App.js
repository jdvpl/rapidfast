import React from 'react';
import {Routes,Route} from 'react-router';
import Inicio from './components/pages/Inicio';
import Menu from './components/pages/Menu';

function App() {
  return (
    <div className="bg-black text-white">
    <Routes>
      <Route path="/" element={<Inicio/>} />
      <Route path="/menu" element={<Menu/>} />
    </Routes>
   </div>
   
  );
}

export default App;
