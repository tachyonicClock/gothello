import React from 'react';

function Cell(props) {

  // Wrapper to the provided onclick handler
  function onClick(e) {
    e.Cell.x = props.x
    e.Cell.y = props.y
    props.onclick(e)
  }

  var stone = <></>
  if (props.stone !== "none" ) {
    stone = <div className={"Stone " + props.stone}></div>
  }
  return (
  <div 
    style={props.style} 
    className={"Cell " + (props.legal? "legal": "illegal")}
    onclick={onClick}>
    {stone}
  </div>)
}

export default Cell;